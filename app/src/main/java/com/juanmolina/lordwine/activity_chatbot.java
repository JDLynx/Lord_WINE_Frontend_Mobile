package com.juanmolina.lordwine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class activity_chatbot extends AppCompatActivity {

    private static final String TAG = "ChatbotActivity";
    // Nota: Es mejor definir la URL base aquí y las rutas específicas en la interfaz ApiService
    private static final String BASE_URL = "https://lord-wine-backend.onrender.com/";
    private static final String USER_ID = "user-lordwine-mobile-1";

    private RecyclerView recyclerViewMessages;
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messageList;
    private TextInputEditText editTextMessage;
    private ImageButton buttonSend;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Inicialización de las vistas
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Configuración del RecyclerView
        messageList = new ArrayList<>();
        adapter = new ChatMessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(adapter);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Saludo inicial del bot
        addInitialBotMessage();

        // Listener para el botón de enviar
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void addInitialBotMessage() {
        String greeting = getGreeting();
        String initialMessageText = greeting + " Soy el asistente virtual de Lord Wine, ¿en qué puedo ayudarte?";
        ChatMessage initialMessage = new ChatMessage(initialMessageText, "bot");
        messageList.add(initialMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return "¡Buenos días!";
        } else if (hour >= 12 && hour < 19) {
            return "¡Buenas tardes!";
        } else {
            return "¡Buenas noches!";
        }
    }

    private void sendMessage() {
        String userMessageText = editTextMessage.getText().toString().trim();
        if (userMessageText.isEmpty()) {
            return;
        }

        // Añadir el mensaje del usuario a la lista
        ChatMessage userMessage = new ChatMessage(userMessageText, "user");
        messageList.add(userMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);

        // Limpiar el campo de texto
        editTextMessage.setText("");

        // Enviar el mensaje al backend
        sendMessageToDialogflow(userMessageText);
    }

    private void sendMessageToDialogflow(String message) {
        DialogflowRequest request = new DialogflowRequest(message, USER_ID);
        Call<DialogflowResponse> call = apiService.dialogflowQuery(request);

        call.enqueue(new Callback<DialogflowResponse>() {
            @Override
            public void onResponse(Call<DialogflowResponse> call, Response<DialogflowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getReply();
                    ChatMessage botMessage = new ChatMessage(botReply, "bot");
                    messageList.add(botMessage);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                    String errorMessage = "Lo siento, hubo un error al conectar con el asistente.";
                    ChatMessage errorMessageObject = new ChatMessage(errorMessage, "bot");
                    messageList.add(errorMessageObject);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<DialogflowResponse> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage(), t);
                String errorMessage = "Lo siento, hubo un error al conectar con el asistente.";
                ChatMessage errorMessageObject = new ChatMessage(errorMessage, "bot");
                messageList.add(errorMessageObject);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            }
        });
    }
}
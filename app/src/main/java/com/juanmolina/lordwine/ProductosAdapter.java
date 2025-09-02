package com.juanmolina.lordwine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;
    private Context context;

    public ProductosAdapter(List<Producto> listaProductos, Context context) {
        this.listaProductos = listaProductos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_card, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.textViewNombre.setText(producto.getProdNombre());
        holder.textViewPrecio.setText("$" + String.format("%.2f", producto.getProdPrecio()));

        if (producto.getProdUrlImagen() != null && !producto.getProdUrlImagen().isEmpty()) {
            Glide.with(context)
                    .load(producto.getProdUrlImagen())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageViewProducto);
        } else {
            holder.imageViewProducto.setImageResource(R.drawable.lordwinelogotexto);
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        TextView textViewPrecio;
        ImageView imageViewProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
        }
    }
}
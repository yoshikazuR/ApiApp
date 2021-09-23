package jp.techacademy.yoshikazu.takahashi.apiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Shop>()

    var onClickAddFavorite: ((Shop) -> Unit)? = null
    var onCLickDeleteFavorite: ((Shop) -> Unit)? = null

    fun refresh(list: List<Shop>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApiItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
    }

    class ApiItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ApiItemViewHolder) {
            updateApiItemViewHolder(holder, position)
        }
    }

    private fun updateApiItemViewHolder(holder: ApiItemViewHolder, position: Int) {
        val data = items[position]
        val isFavorite = FavoriteShop.findBy(data.id) != null
        holder.apply {
            rootView.apply {
                setBackgroundColor(ContextCompat.getColor(context, if (position%2==0) android.R.color.white else android.R.color.darker_gray))
            }
            nameTextView.text = data.name
            Picasso.get().load(data.logoImage).into(imageView)
            favoriteImageView.apply {
                setImageResource(R.drawable.ic_star_border)
                setOnClickListener{
                    if (isFavorite) {
                        onCLickDeleteFavorite?.invoke(data)
                    }else {
                        onClickAddFavorite?.invoke(data)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }
}
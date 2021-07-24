package com.developerachu.moviesapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerachu.moviesapp.R
import com.developerachu.moviesapp.interfaces.OnTvShowClickListener
import com.developerachu.moviesapp.models.TvShow

class PopularTvShowsListAdapter(
    private val context: Context,
    private val popularTvShowsList: ArrayList<TvShow>,
    private var clickListener: OnTvShowClickListener? = null
) : RecyclerView.Adapter<PopularTvShowsListAdapter.TvShowHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowHolder {
        return TvShowHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.popular_tv_show_item, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: TvShowHolder, position: Int) {
        val tvShow = popularTvShowsList[position]
        holder.bind(context, tvShow)
    }

    override fun getItemCount() = popularTvShowsList.size


    fun setTvShowClickListener(clickListener: OnTvShowClickListener) {
        this.clickListener = clickListener
    }

    class TvShowHolder(v: View, clickListener: OnTvShowClickListener?) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        private val popularTvShowItem: ConstraintLayout = v.findViewById(R.id.popular_tv_show_item)
        private val tvShowImageView: ImageView = v.findViewById(R.id.tv_show_image_view)
        private val tvShowNameTextView: TextView = v.findViewById(R.id.tv_show_name_text_view)
        private val tvShowPopularityTextView: TextView =
            v.findViewById(R.id.tv_show_popularity_text_view)
        private val tvShowVoteAverageTextView: TextView =
            v.findViewById(R.id.tv_show_vote_average_text_view)
        private val tvShowDateTextView: TextView = v.findViewById(R.id.tv_show_date_text_view)
        private var clickListener: OnTvShowClickListener? = null

        init {
            popularTvShowItem.setOnClickListener(this)
            this.clickListener = clickListener
        }

        fun bind(context: Context, tvShow: TvShow) {
            Glide.with(context).load(tvShow.imageUrl).into(tvShowImageView)
            tvShowNameTextView.text = tvShow.name
            tvShowPopularityTextView.text = tvShow.popularity
            tvShowVoteAverageTextView.text = tvShow.averageVote
            tvShowDateTextView.text = tvShow.airDate
        }

        override fun onClick(v: View) {
            clickListener?.tvShowItemClicked(v, bindingAdapterPosition)
        }
    }

}

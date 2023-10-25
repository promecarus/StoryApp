package com.promecarus.storyapp.custom.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.promecarus.storyapp.R.string.from
import com.promecarus.storyapp.data.model.Story
import com.promecarus.storyapp.databinding.ItemStoryBinding
import com.promecarus.storyapp.ui.DetailActivity
import com.promecarus.storyapp.utils.HelperUtils.getCity
import com.promecarus.storyapp.utils.HelperUtils.getTimeAgo
import com.promecarus.storyapp.utils.HelperUtils.loadWithCircularProgress

class StoryAdapter(private val context: Context) :
    PagingDataAdapter<Story, StoryAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Story, newItem: Story) = oldItem.id == newItem.id
    }) {
    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) = bind(binding, story)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStoryBinding.inflate(from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    private fun bind(binding: ItemStoryBinding, story: Story) = binding.apply {
        story.apply {
            tvItemName.text = name
            tvItemCreated.text = buildString {
                append(getTimeAgo(root.context, createdAt))
                append((getCity(context, lat, lon)?.let {
                    context.getString(
                        from, it
                    )
                } ?: ""))
            }
            loadWithCircularProgress(context, photoUrl, ivItemPhoto)
            tvItemDescription.text = description
            root.setOnClickListener {
                it.context.startActivity(
                    Intent(context, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_STORY, story)
                    }, makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(mcvItemStory, "card"),
                        Pair(tvItemName, "name"),
                        Pair(tvItemCreated, "created"),
                        Pair(ivItemPhoto, "photo"),
                        Pair(tvItemDescription, "description"),
                    ).toBundle()
                )
            }
        }
    }
}

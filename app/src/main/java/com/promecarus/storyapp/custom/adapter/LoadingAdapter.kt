package com.promecarus.storyapp.custom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.promecarus.storyapp.databinding.ItemLoadingBinding

class LoadingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) = bind(binding, loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder =
        ViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry
        )

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    private fun bind(binding: ItemLoadingBinding, loadState: LoadState) = binding.apply {
        if (loadState is LoadState.Error) {
            tvError.text = loadState.error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        btnRetry.isVisible = loadState is LoadState.Error
        tvError.isVisible = loadState is LoadState.Error
    }
}

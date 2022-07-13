package com.tallence.newsapp

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("normalString")
fun TextView.setNormalString(s: String?) {
    s?.let {
        text = s
    }
}

@BindingAdapter("dateString")
fun TextView.setDateString(s: String?) {
    s?.let {
        text = s.replace('T', ' ').replace('Z', ' ').dropLast(4)
    }
}


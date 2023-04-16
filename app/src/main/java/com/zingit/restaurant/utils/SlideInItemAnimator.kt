package com.zingit.restaurant.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class SlideInItemAnimator : DefaultItemAnimator() {

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        // Disable the default change animation
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        // Animate the removal of the first item
        if (holder.adapterPosition == 0) {
            val animator = ObjectAnimator.ofFloat(holder.itemView, View.TRANSLATION_X, -holder.itemView.width.toFloat())
            animator.duration = 500
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchRemoveFinished(holder)
                }
            })
            animator.start()
            return false
        } else {
            // Disable the default remove animation for the second item
            dispatchRemoveFinished(holder)
            return false
        }
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        // Animate the addition of the second item
        if (holder.adapterPosition == 1) {
            holder.itemView.translationX = -holder.itemView.width.toFloat()
            val animator = ObjectAnimator.ofFloat(holder.itemView, View.TRANSLATION_X, 0f)
            animator.duration = 500
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchAddFinished(holder)
                }
            })
            animator.start()
            return false
        } else {
            // Disable the default add animation for the first item
            dispatchAddFinished(holder)
            return false
        }
    }
}

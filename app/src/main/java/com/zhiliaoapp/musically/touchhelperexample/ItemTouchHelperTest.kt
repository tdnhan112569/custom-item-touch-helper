package com.zhiliaoapp.musically.touchhelperexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.provider.CalendarContract
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class ItemTouchHelperTest(private val recyclerView: RecyclerView,
                          private val listPositionedDeniedSwipe: List<Int> = listOf()) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT), View.OnTouchListener {

    lateinit var toast: Toast

    @SuppressLint("ResourceType")
    val listButton = listOf(
        UnderlayButton(recyclerView.context, ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_baseline_delete_24)!!, Color.DKGRAY, object : UnderlayButtonClickListener {
            override fun onClick(position: Int, itemView: View) {
                if(this@ItemTouchHelperTest::toast.isInitialized) {
                    toast.cancel()
                }
                toast = Toast.makeText(recyclerView.context, "Delete click $position", Toast.LENGTH_LONG)
                toast.show()
                log("Delete clicked $position")
            }
        }),
        UnderlayButton(recyclerView.context, ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_baseline_create_24)!!, Color.MAGENTA, object : UnderlayButtonClickListener {
            override fun onClick(position: Int, itemView: View) {
                if(this@ItemTouchHelperTest::toast.isInitialized) {
                    toast.cancel()
                }
                toast = Toast.makeText(recyclerView.context, "New click $position", Toast.LENGTH_LONG)
                toast.show()
                log("New clicked $position")
            }
        })
    )

    private var currentSwipedLeftViewHolder: RecyclerView.ViewHolder? = null
    private var currentLeftItemPosition: Int? = null
    private var lastPositionSwipe = 0
    private var swipeLeftCount = 0
    private val widthOfUnderlayButton = listButton.intrinsicWidth()

    init {
        ItemTouchHelper(this).attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                clearLastViewHolder()
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        recyclerView.setOnTouchListener(this)
    }

    fun isClearedSwipeView()
        = currentSwipedLeftViewHolder != null || currentLeftItemPosition != null

    fun clearLastViewHolder() {
        currentSwipedLeftViewHolder?.let {
            val clearPosition = if(it.adapterPosition  < 0) {
                currentLeftItemPosition ?: -1
            }else {
                it.adapterPosition
            }
            if(clearPosition != -1) {
                clearView(recyclerView, it)
                recyclerView.adapter?.notifyItemChanged(clearPosition)
                currentSwipedLeftViewHolder = null
                currentLeftItemPosition = null
                swipeLeftCount = 0
                log("clearLastViewHolder success: $clearPosition")
            }
        }
    }

    private fun log(mes: String) {
        Log.e("ItemTouchHelperTest", mes)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        log("getDragDirs: ${viewHolder.adapterPosition}")
        if(listPositionedDeniedSwipe.contains(viewHolder.adapterPosition)){
            return ItemTouchHelper.ACTION_STATE_IDLE
        }
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if(listPositionedDeniedSwipe.contains(viewHolder.adapterPosition)) {
            clearLastViewHolder()
            return
        }
        swipeLeftCount++
        if(lastPositionSwipe == viewHolder.adapterPosition) {
            if(swipeLeftCount.mod(2) == 0) {
                clearLastViewHolder()
            }
        } else {
            clearLastViewHolder()
            swipeLeftCount = 1
        }
        when(direction) {
            ItemTouchHelper.LEFT -> {
                log("onSwipe: LEFT, ${getSwipeThreshold(viewHolder)}")
                currentSwipedLeftViewHolder = viewHolder
                currentLeftItemPosition = viewHolder.adapterPosition
                log("onSwipe: case 1, position: $currentLeftItemPosition")
            }
            ItemTouchHelper.RIGHT -> {
                log("onSwipe: RIGHT, ${getSwipeThreshold(viewHolder)}")
                clearLastViewHolder()
                log("onSwipe: case 2, position: $currentLeftItemPosition")
            }
        }
        lastPositionSwipe = viewHolder.adapterPosition
        log("\n\n\n\n\n")
    }



    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if(listPositionedDeniedSwipe.contains(viewHolder.adapterPosition)) return
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val fromX = when {
                dX < -50f -> {
                    -widthOfUnderlayButton
                }
                dX > -50f -> {
                    0f
                }
                else -> dX
            }
            drawButtons(c, listButton, viewHolder.itemView, fromX)
            super.onChildDraw(c, recyclerView, viewHolder, fromX, dY, actionState, isCurrentlyActive)
            log("----------------------onChildDraw ---------------------------")
            log("$viewHolder")
            log("position: ${viewHolder.adapterPosition}")
            log("dX: $dX")
            log("-------------------------------------------------------------")
            log("       --------------------------------------------")
            log("                  -----------------------")
            log("                           -------")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent): Boolean {
        log("onTouch")
        listButton.checkRegionClickByCoordinates(p1)
        return false
    }

    @Synchronized
    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        dX: Float
    ) {

        var right = itemView.right
        buttons.forEach { button ->
            val width = button.intrinsicWidth / buttons.intrinsicWidth() * abs(dX)
            val left = right - width
            button.draw(
                canvas,
                RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
            )
            right = left.toInt()
        }
    }

    interface UnderlayButtonClickListener {
        fun onClick(position: Int, itemView: View)
    }

    inner class UnderlayButton(
        private val context: Context,
        private val icon: Drawable,
        @ColorRes private val colorRes: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var clickableRegion: RectF? = null
        val intrinsicWidth = convertDpToPx(50f)

        @SuppressLint("ResourceAsColor")
        fun draw(canvas: Canvas, rect: RectF) {
            val paint = Paint()

            // Draw background
            paint.color = colorRes//ContextCompat.getColor(context, colorRes)
            canvas.drawRect(rect, paint)
            val width = convertDpToPx(25f).toInt()
            if(width >= rect.right - rect.left) {
                return
            }
            val bitmap = convertToBitmap(icon,width,width)
            canvas.drawBitmap(bitmap,rect.centerX()-bitmap.width/2,rect.centerY()-bitmap.height/2,paint)
            clickableRegion = rect
        }
        private fun convertToBitmap(drawable: Drawable, widthPixels: Int, heightPixels: Int): Bitmap {
            val mutableBitmap =
                Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(mutableBitmap)
            drawable.setBounds(
                0,
                0,
                widthPixels,
                heightPixels
            )
            drawable.draw(canvas)
            return mutableBitmap
        }
        fun handle(event: MotionEvent) {
            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {
                    currentSwipedLeftViewHolder?.let { viewHolder ->
                        currentLeftItemPosition?.let { position ->
                            clickListener.onClick(position, viewHolder.itemView)
                            clearLastViewHolder()
                            log("Click action detected")
                        }
                    }
                }
            }
        }
    }
    //endregion

    fun convertDpToPx(dpInput: Float): Float {
        return (dpInput * Resources.getSystem().displayMetrics.density)
    }

    private fun List<UnderlayButton>.intrinsicWidth(): Float {
        if (isEmpty()) return 0.0f
        return map { it.intrinsicWidth }.reduce { acc, fl -> acc + fl }
    }

    private fun List<UnderlayButton>.checkRegionClickByCoordinates(event: MotionEvent) {
        this.forEach { it.handle(event) }
    }
}
package com.zhiliaoapp.musically.touchhelperexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var rv : RecyclerView
    lateinit var itemTouchHelperTest: ItemTouchHelperTest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = this.findViewById(R.id.rvItem)
        rv.layoutManager = LinearLayoutManager(
            this,
        )
        val list = LinkedList((1..100).map { String()})
        rv.adapter = RecyclerViewHolder(this, list)
        itemTouchHelperTest = ItemTouchHelperTest(rv, (10..20).map { it })
        Log.d("Hello", "start call")
        testInlinedFunction {
            return@testInlinedFunction
        }
        Log.d("Hello", "end call")
    }

    fun clearCurrentSwipeView() : Boolean {
        val isExist = itemTouchHelperTest.isClearedSwipeView()
        itemTouchHelperTest.clearLastViewHolder()
        return isExist
    }

    private inline fun testInlinedFunction(crossinline body: () -> Unit) {
        Log.d("Hello", "Hello :v")
        body.invoke()
        Log.d("Hello", "Hello 2:v")
    }
}
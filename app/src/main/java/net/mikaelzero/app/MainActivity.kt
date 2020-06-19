package net.mikaelzero.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import net.mikaelzero.app.local.LocalImageActivity

class MainActivity : AppCompatActivity() {
    var texts = arrayOf(
        "load normal",
        "load local image"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = MainAdapter()
        recyclerView!!.itemAnimator = DefaultItemAnimator()
    }

    internal inner class MainAdapter : RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(
                    this@MainActivity
                ).inflate(
                    R.layout.item_main, parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.tv.text = texts[position]
            holder.cardView.setOnClickListener {
                if (position == 1) {
                    startActivity(Intent(this@MainActivity, LocalImageActivity::class.java))
                } else {
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    DisplayActivity.newIntent(this@MainActivity, bundle)
                }
            }
        }

        override fun getItemCount(): Int {
            return texts.size
        }

        internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var tv: TextView
            var cardView: CardView

            init {
                tv = view.findViewById<View>(R.id.tv) as TextView
                cardView = view.findViewById<View>(R.id.cardView) as CardView
            }
        }
    }
}
package net.mikaelzero.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    String[] texts = new String[]{
            "加载列表",
            "加载单张",
            "加载长图",
            "加载视频"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainAdapter());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.item_main, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tv.setText(texts[position]);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View srcView) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    DisplayActivity.newIntent(MainActivity.this, bundle);

                }
            });
        }

        @Override
        public int getItemCount() {
            return texts.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            CardView cardView;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv);
                cardView = (CardView) view.findViewById(R.id.cardView);
            }
        }
    }

}

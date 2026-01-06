package co.com.mypt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import co.com.mypt.onBoarding.weightClass.Screen;
import co.com.mypt.R;


public class WeightScaleAdapter extends RecyclerView.Adapter<WeightScaleAdapter.ViewHolder> {

    private final List<String> data = new ArrayList<>();

    private int colour = Color.parseColor("#FF6200EE");


    private int centerHeight = 33;
    private int level1Height = 30;
    private int level2Height = 27;
    private int level3Height = 24;
    private int shortSpokeHeight = 20;
    private float centerText = 18f;
    private float level1Text = 16f;
    private float level2Text = 14f;
    private float level3Text = 12f;
    private static final int MAX_WAVE_DISTANCE = 3;
    private int centerPosition = RecyclerView.NO_POSITION;

    Context contet;
    public WeightScaleAdapter(Context applicationContext) {
        this.contet=applicationContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_scale_item, parent, false);
        return new ViewHolder(itemView);
    }

    /*@Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.spoke.getLayoutParams();

        int distance = Math.abs(position - selectedPosition);

        holder.spoke.setBackgroundColor(colour);
        holder.value.setVisibility(View.VISIBLE);
        holder.value.setText(data.get(position));
       *//* if (position == selectedPosition && shouldDrawLongSpoke(position)) {
            // 🔥 CENTER ITEM (largest)
            params.height = Screen.dpToPx(holder.itemView.getContext(), longerSpokeHeight);
            params.width = Screen.dpToPx(holder.itemView.getContext(), 2);
            holder.spoke.setBackgroundColor(
                    contet.getResources().getColor(R.color.rulerlargeline)
            );
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(data.get(position));
            holder.value.setTextColor(contet.getResources().getColor(R.color.available));
            holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
        } else if (shouldDrawLongSpoke(position)) {
            // first position, last position, divisions of every nth unit
            holder.value.setText(data.get(position));
            holder.value.setVisibility(View.VISIBLE);
            params.height = Screen.dpToPx(holder.itemView.getContext(), longSpokeHeight);
            params.width = Screen.dpToPx(holder.itemView.getContext(), 2);
            holder.spoke.setBackgroundColor(contet.getResources().getColor(R.color.rulerlargeline));
            holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        } else {
            // otherwise draw a space to give it a standard width wrt to standard text size
            holder.value.setText(" ");
            holder.value.setVisibility(View.INVISIBLE);
            params.height = Screen.dpToPx(holder.itemView.getContext(), shortSpokeHeight);
            params.width = Screen.dpToPx(holder.itemView.getContext(), 2);
            holder.spoke.setBackgroundColor(contet.getResources().getColor(R.color.rulersmallline));

        }*//*
        if (shouldDrawLongSpoke(position)) {

            if (distance == 0) {
                // 🔥 CENTER
                params.height = Screen.dpToPx(contet, centerHeight);
                holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, centerText);
                holder.spoke.setBackgroundColor(contet.getColor(R.color.rulerlargeline));

            } else if (distance == 1) {
                params.height = Screen.dpToPx(contet, level1Height);
                holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, level1Text);

            } else if (distance == 2) {
                params.height = Screen.dpToPx(contet, level2Height);
                holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, level2Text);

            } else if (distance == 3) {
                params.height = Screen.dpToPx(contet, level3Height);
                holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, level3Text);

            } else {
                // Outside wave range
                params.height = Screen.dpToPx(contet, shortSpokeHeight);
                holder.value.setVisibility(View.INVISIBLE);
            }

        } else {
            // Small spoke
            params.height = Screen.dpToPx(contet, shortSpokeHeight);
            holder.value.setVisibility(View.INVISIBLE);
            holder.spoke.setBackgroundColor(contet.getColor(R.color.rulersmallline));
        }

        params.width = Screen.dpToPx(contet, 2);
        holder.spoke.setLayoutParams(params);
        float scale =
                1f - Math.min(distance, 3) * 0.12f;
        holder.spoke.animate()
                .scaleY(scale)
                .setDuration(150)
                .start();
        holder.spoke.setLayoutParams(params);
    }*/

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        int heightDp = shortSpokeHeight;

       /* if (centerPosition != RecyclerView.NO_POSITION) {
            int diff = Math.abs(position - centerPosition);

            switch (diff) {
                case 0:
                    heightDp = centerHeight;
                    break;
                case 1:
                    heightDp = level1Height;
                    break;
                case 2:
                    heightDp = level2Height;
                    break;
                case 3:
                    heightDp = level3Height;
                    break;
                default:
                    heightDp = shortSpokeHeight;
            }
        }

        ViewGroup.LayoutParams lp = holder.spoke.getLayoutParams();
        lp.height = Screen.dpToPx(contet,heightDp);
        holder.spoke.setLayoutParams(lp);
*/

        holder.spoke.setBackgroundColor(
                contet.getResources().getColor(R.color.rulerlargeline)
        );


        // -------- TEXT ONLY FOR 10th ITEM --------
        if (isMajorTick(position)) {
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(data.get(position));
        } else {
            holder.value.setVisibility(View.INVISIBLE);
        }

       /* holder.value.setTextSize(TypedValue.COMPLEX_UNIT_SP, centerPosition == position ? 15f : 14f);
         if (centerPosition == position){
             holder.value.setTextColor(contet.getResources().getColor(R.color.available));
         }else{
             holder.value.setTextColor(contet.getResources().getColor(R.color.rulartextcolor));
         }*/

    }
     public boolean isMajorTick(int index) {
        return index % 10 == 0;
    }


    private boolean shouldDrawLongSpoke(int index) {
      /*  if (index == 0) {
            return true;
        }

        if (index == data.size() - 1) {
            return true;
        }

        // since we always start at 1, we have to account for this when using the *index*
        // in order to get the spoke position
        return (index + 1) % countBetweenMarkers == 0;*/
        return index % 10 == 0;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setSpokeColour(String hex) {
        this.colour = Color.parseColor(hex);
    }

    public void setSpokeColour(int colour) {
        this.colour = colour;
    }

   /* public void setCountBetweenMarkers(int countBetweenMarkers) {
        this.countBetweenMarkers = countBetweenMarkers;
    }

    public void setLongSpokeHeight(int longSpokeHeight) {
        this.longSpokeHeight = longSpokeHeight;
    }
*/
    public void setShortSpokeHeight(int shortSpokeHeight) {
        this.shortSpokeHeight = shortSpokeHeight;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView value;
       public  View spoke;

        ViewHolder(View v) {
            super(v);
            value = v.findViewById(R.id.unit_value);
            spoke = v.findViewById(R.id.unit_spoke);
        }
    }
    private int selectedPosition = RecyclerView.NO_POSITION;

    public void setSelectedPosition(int position) {
        if (position == selectedPosition) return;

        int old = selectedPosition;
        selectedPosition = position;

        notifyWaveRange(old);
        notifyWaveRange(selectedPosition);
    }
    private void notifyWaveRange(int center) {
        if (center == RecyclerView.NO_POSITION) return;

        int start = Math.max(0, center - 3);
        int end = Math.min(getItemCount() - 1, center + 3);

        notifyItemRangeChanged(start, end - start + 1);
    }

    public void setCenterPosition(int position) {
        if (centerPosition == position) return;

        int oldCenter = centerPosition;
        centerPosition = position;

        if (oldCenter != RecyclerView.NO_POSITION) {
            notifyItemRangeChanged(oldCenter - 3, 7);
        }
        notifyItemRangeChanged(centerPosition - 3, 7);
    }

}

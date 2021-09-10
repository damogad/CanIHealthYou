package com.example.canihealthyou;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.myViewHolder> {

    private ArrayList<StepItem> stepsData;
    private Activity mActivity;
    ArrayList<StepsAdapter.myViewHolder> listOfStepViews;

    public StepsAdapter(ArrayList<StepItem> stepsData, Activity activity) {
        this.stepsData = stepsData;
        this.mActivity = activity;
        listOfStepViews = new ArrayList<>();
    }

    @Override
    public StepsAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View _v = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_card_item, parent, false );
        return new StepsAdapter.myViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(StepsAdapter.myViewHolder holder, int position) {
        StringBuilder s = new StringBuilder();
        StepItem item = stepsData.get(position);
        if (item.getHours() > 0)
            s.append(item.getHours() + "h ");
        if (item.getMinutes() > 0)
            s.append(item.getMinutes() + "min ");
        if (item.getSeconds() > 0)
            s.append(item.getSeconds() + mActivity.getResources().getString(R.string.Step_Seconds));
        holder.requiredTime.setText(s.toString());
        holder.numberStep.setText(String.valueOf(position + 1));
        holder.stepDescription.setText(item.getDescription());
        listOfStepViews.add(holder);
    }

    @Override
    public int getItemCount() {
        return this.stepsData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView numberStep, stepDescription, requiredTime;

        public myViewHolder(View itemView) {
            super(itemView);
            numberStep = itemView.findViewById(R.id.step_number);
            stepDescription = itemView.findViewById(R.id.step_description);
            requiredTime = itemView.findViewById(R.id.step_required_time);
        }
    }
}

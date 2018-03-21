package ca.polymtl.inf3995.oronos.parser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.GlobalParameters;

public class DisplayLogAdapter extends RecyclerView.Adapter<DisplayLogAdapter.ViewHolder> {

    private Context context;
    private List<String> csvMsgs;
    private int beginningOfListIndex;

    public DisplayLogAdapter(Context context) {
        this.context = context;
        csvMsgs = new ArrayList<>();
        beginningOfListIndex = 0;
    }

    public void addCSVMsg(String csvMsg) {
        if (csvMsgs.size() < GlobalParameters.LIMIT_OF_N_MSG) {
            csvMsgs.add(csvMsg);
        } else {
            csvMsgs.set(beginningOfListIndex, csvMsg);
            beginningOfListIndex = beginningOfListIndex + 1 % GlobalParameters.LIMIT_OF_N_MSG;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false
        );
        return ViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = csvMsgs.get(position + beginningOfListIndex % GlobalParameters.LIMIT_OF_N_MSG);
        holder.setText(text);
    }

    @Override
    public int getItemCount() {
        return csvMsgs.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private static ViewHolder newInstance(View itemView) {
            TextView textView = itemView.findViewById(android.R.id.text1);
            return new ViewHolder(itemView, textView);
        }

        private ViewHolder(View itemView, TextView textView) {
            super(itemView);
            this.textView = textView;
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}

package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.utils.GlobalParameters;

public class DisplayLogAdapter extends RecyclerView.Adapter<DisplayLogAdapter.ViewHolder> {

    private Context context;
    private List<String> csvMsgs;

    public DisplayLogAdapter(Context context) {
        this.context = context;
        csvMsgs = new ArrayList<>();
    }

    public void addCSVMsg(String csvMsg) {
        if (csvMsgs.size() == GlobalParameters.LIMIT_OF_N_MSG) {
            csvMsgs.remove(0);
        }
        csvMsgs.add(csvMsg);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.data_logs_scroll, parent, false);
        return ViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StringBuffer text = new StringBuffer("");
        for (String msg : csvMsgs) {
            text.append(msg);
        }
        holder.setText(text.toString());
        holder.scrollToBottom();
    }

    @Override
    public int getItemCount() {
        return csvMsgs.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ScrollView scrollView;

        private static ViewHolder newInstance(View itemView) {
            TextView textView = itemView.findViewById(R.id.TEXT_STATUS_ID);
            ScrollView scrollView = itemView.findViewById(R.id.SCROLLER_ID);
            return new ViewHolder(itemView, textView, scrollView);
        }

        private ViewHolder(View itemView, TextView textView, ScrollView scrollview) {
            super(itemView);
            this.textView = textView;
            this.scrollView = scrollview;
        }

        public void setText(String text) {
            textView.setText(text);
        }

        // method from : https://stackoverflow.com/questions/1748977/making-textview-scrollable-on-android
        public void scrollToBottom()
        {
            scrollView.post(new Runnable()
            {
                public void run()
                {
                    scrollView.smoothScrollTo(0, textView.getBottom());
                }
            });
        }
    }
}

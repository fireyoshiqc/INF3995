package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.fragments.MiscFilesFragment;


public class MiscFilesAdapter extends RecyclerView.Adapter<MiscFilesAdapter.ItemContainer> {
    private List<String>      items;
    private MiscFilesFragment parentFragment;

    private final Object lock = new Object();

    public MiscFilesAdapter(List<String> items, MiscFilesFragment parentFragment) {
        this.items = items;
        this.parentFragment = parentFragment;
    }

    @Override
    public MiscFilesAdapter.ItemContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.misc_files_textview, parent, false);

        return new MiscFilesAdapter.ItemContainer(itemView);
    }

    @Override
    public void onBindViewHolder(ItemContainer holder, int position) {
        String filename = this.items.get(position);
        holder.fileItem.setText(filename);
    }

    @Override
    public void onViewRecycled(ItemContainer holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(String item) {
        synchronized (this.lock) {
            this.items.add(item);
        }
    }

    public void addAll(List<String> items) {
        synchronized (this.lock) {
            this.items.addAll(items);
        }
    }

    public void clear() {
        synchronized (this.lock) {
            this.items.clear();
        }
    }

    class ItemContainer extends RecyclerView.ViewHolder {
        private TextView fileItem;

        ItemContainer(final View view) {
            super(view);
            this.fileItem = (TextView)view;
            this.fileItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView)v;
                    String fileToDownload = textView.getText().toString();
                    parentFragment.downloadAndOpenFile(fileToDownload);
                }
            });
        }

    }

}

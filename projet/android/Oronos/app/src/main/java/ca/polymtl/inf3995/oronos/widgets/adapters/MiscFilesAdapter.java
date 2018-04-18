package ca.polymtl.inf3995.oronos.widgets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.polymtl.inf3995.oronos.R;
import ca.polymtl.inf3995.oronos.fragments.MiscFilesFragment;

/**
 * <h1>Misc Files Adapter</h1>
 * This Adapter allows to display a list containing strings corresponding to pdf names.
 * A click onto an element of this list provokes the download of the pdf.
 *
 * @author Félix Boulet, Charles Hosson
 * @version 0.0
 * @since 2018-04-12
 */
public class MiscFilesAdapter extends RecyclerView.Adapter<MiscFilesAdapter.ItemContainer> {
    private List<String>      items;
    private MiscFilesFragment parentFragment;

    private final Object lock = new Object();

    /**
     * Constructor that stores a list of pdf names and the fragment responsible of this adapter.
     *
     * @param items list of strings corresponding to pdf names.
     * @param parentFragment the fragment responsible of the Misc Files Adapter.
     * */
    public MiscFilesAdapter(List<String> items, MiscFilesFragment parentFragment) {
        this.items = items;
        this.parentFragment = parentFragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MiscFilesAdapter.ItemContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.misc_files_textview, parent, false);

        return new MiscFilesAdapter.ItemContainer(itemView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(ItemContainer holder, int position) {
        String filename = this.items.get(position);
        holder.fileItem.setText(filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRecycled(ItemContainer holder) {
        super.onViewRecycled(holder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * This method is refreshing the list of pdf by adding one item.
     *
     * @param item a string corresponding to a pdf name.
     * */
    public void add(String item) {
        synchronized (this.lock) {
            this.items.add(item);
        }
    }

    /**
     * This method is refreshing the list of pdf by adding many items.
     *
     * @param items a list of strings corresponding to pdf names.
     * */
    public void addAll(List<String> items) {
        synchronized (this.lock) {
            this.items.addAll(items);
        }
    }

    /**
     * This method removes all items from the list of pdf names.
     * */
    public void clear() {
        synchronized (this.lock) {
            this.items.clear();
        }
    }

    /**
     * This class associates a text view to an element of the list of pdf names.
     * */
    class ItemContainer extends RecyclerView.ViewHolder {
        private TextView fileItem;

        /**
         * Constructor of an Item Container that declares a OnClick listener.
         * On a click onto the text view, the pdf represented by the text view must be
         * downloaded.
         *
         * @param view The text view that is going to be displayed.
         * */
        ItemContainer(final View view) {
            super(view);
            this.fileItem = (TextView)view;
            this.fileItem.setOnClickListener(new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 * */
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

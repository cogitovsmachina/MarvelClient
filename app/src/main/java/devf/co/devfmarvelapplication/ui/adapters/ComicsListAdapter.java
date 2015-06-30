package devf.co.devfmarvelapplication.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import devf.co.devfmarvelapplication.R;
import devf.co.devfmarvelapplication.model.Comic;
import devf.co.devfmarvelapplication.rest.Constants;
import devf.co.devfmarvelapplication.rest.MarvelApiClient;
import devf.co.devfmarvelapplication.rest.models.ComicsListResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sati on 29/04/2015.
 */
public class ComicsListAdapter extends RecyclerView.Adapter<ComicsListAdapter.ComicViewHolder> {

    private static final int VIEW_PROGRESS = 0;
    private static final int VIEW_COMIC = 1;

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Comic> comics;

    public ComicsListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        comics = new ArrayList<>();
        comics.add(null);
        comics.add(null);
    }

    @Override
    public int getItemViewType(int position) {
        return comics.get(position) != null ? VIEW_COMIC : VIEW_PROGRESS;

    }

    @Override
    public ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_COMIC) {
            View itemView = layoutInflater.inflate(R.layout.item_comic, parent, false);
            return new ComicViewHolder(itemView, viewType);
        } else {
            View itemView = layoutInflater.inflate(R.layout.item_comic_progress, parent, false);
            return new ComicViewHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ComicViewHolder holder, int position) {

        if (position < getItemCount() - 2)
            holder.setComicData(comics.get(position));
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    public void requestForMoreComics() {
        MarvelApiClient.getInstance(context)
                .requestComicsList(Constants.CHARACTERS_LIMIT, getComicsItemsCount(), new Callback<ComicsListResponse>() {
                    @Override
                    public void success(ComicsListResponse comicsListResponse, Response response) {
                        updateList(comicsListResponse.getComics());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
    }

    public void updateList(List<Comic> comics) {
        this.comics.addAll(getComicsItemsCount(), comics);
        notifyDataSetChanged();
    }

    private int getComicsItemsCount() {
        return comics.size() - 2;
    }

    public class ComicViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.img_comic)
        SimpleDraweeView imgComic;

        @InjectView(R.id.textViewTitleComic)
        TextView txtTItle;


        @InjectView(R.id.textViewPagesComic)
        TextView txtPages;

        @InjectView(R.id.textViewIssueComic)
        TextView txtIssue;


        public ComicViewHolder(View itemView, int type) {
            super(itemView);

            if (type == VIEW_COMIC) {
                ButterKnife.inject(this, itemView);
            }
        }

        public void setComicData(Comic comic) {

            Uri urlImage = comic.getUrlImage();
            if (!urlImage.equals(Uri.EMPTY))
                imgComic.setImageURI(urlImage);

            txtTItle.setText(comic.getTitle());

            txtIssue.setText("Issue # " + comic.getIssues());

            txtPages.setText("Pag. " + comic.getPages());

        }

    }
}

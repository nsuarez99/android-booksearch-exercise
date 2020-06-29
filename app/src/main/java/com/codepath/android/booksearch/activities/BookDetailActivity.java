package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.android.booksearch.GlideApp;
import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private Toolbar toolbar;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;
    private Book book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        toolbar = findViewById(R.id.toolbar);

        // Extract book object from intent extras
        // Use book object to populate data into views
        book = (Book) Parcels.unwrap(getIntent().getParcelableExtra("book"));
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        Glide.with(BookDetailActivity.this).load(book.getCoverUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("BookDetailActivity", "image load failure");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                prepareShareIntent(((BitmapDrawable) resource).getBitmap());
                attachShareIntentAction();
                return false;
            }
        }).into(ivBookCover);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(book.getTitle());
    }


    // Gets the image URI and setup the associated share intent to hook into the provider
    public void prepareShareIntent(Bitmap drawableImage) {

        // getExternalFilesDir() + "/Pictures" should match the declaration in fileprovider.xml paths
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            drawableImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("BookDetailActivity", "prepare share file not found failing");
            e.printStackTrace();
        }
        catch (IOException e){
            Log.e("BookDetailActivity", "prepare share io exception");
            e.printStackTrace();
        }

        // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
        Uri bmpUri = FileProvider.getUriForFile(BookDetailActivity.this, "com.codepath.fileprovider", file);

        // Construct share intent as described above based on bitmap
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, book.getTitle());
        shareIntent.setType("image/*");
    }

    // Attaches the share intent to the share menu item provider
    public void attachShareIntentAction() {
        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction();
        // Return true to display menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

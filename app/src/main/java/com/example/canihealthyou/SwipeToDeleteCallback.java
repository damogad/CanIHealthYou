package com.example.canihealthyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    //CalendarAdapter mAdapter;
    RecyclerView.Adapter mAdapter;
    private Drawable deleteIcon;
    private final ColorDrawable deleteBackground;
    private Drawable editIcon;
    private final ColorDrawable editBackground;
    private Context context;

    private static int decideDirection(RecyclerView.Adapter adapter) {
        if (adapter instanceof CalendarAdapter) {
            return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        else if (adapter instanceof RecipesAdapter) {
            return ItemTouchHelper.LEFT;
        }
        return ItemTouchHelper.LEFT;
    }

    public SwipeToDeleteCallback(RecyclerView.Adapter adapter, Context context) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, decideDirection(adapter));
        this.context = context;
        mAdapter = adapter;
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_36dp);
        deleteBackground = new ColorDrawable(Color.RED);
        editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_36dp);
        editBackground = new ColorDrawable(context.getResources().getColor(R.color.blueColorPrimary));

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
        int positionDragged = dragged.getAdapterPosition();
        int positionTarget = target.getAdapterPosition();
        if (mAdapter instanceof CalendarAdapter)
            ((CalendarAdapter)mAdapter).moveItem(positionDragged, positionTarget);
        else if (mAdapter instanceof RecipesAdapter)
            ((RecipesAdapter)mAdapter).moveItem(positionDragged, positionTarget);

        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (mAdapter instanceof CalendarAdapter) {
            if (direction == ItemTouchHelper.RIGHT)
                ((CalendarAdapter) mAdapter).deleteItem(position);
            else if (direction == ItemTouchHelper.LEFT)
                ((CalendarAdapter) mAdapter).editItem(position);
        }
        else if (mAdapter instanceof RecipesAdapter)
            ((RecipesAdapter)mAdapter).deleteItem(position);
        SharedPreferences prefs = context.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
            viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            try {
                Thread.sleep(20);
            }
            catch (InterruptedException e) {

            }
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

        int deleteBackgroundCornerOffset = 20; //so deleteBackground is behind the rounded corners of itemView

        int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int deleteIconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

        if (mAdapter instanceof CalendarAdapter) {
            if (dX > 0) { // Swiping to the right
                int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
                int deleteIconRight = deleteIconLeft + deleteIcon.getIntrinsicWidth();
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);

                deleteBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + deleteBackgroundCornerOffset, itemView.getBottom());
                deleteBackground.draw(c);
                deleteIcon.draw(c);
            } else if (dX < 0) { // Swiping to the left
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                editIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);

                editBackground.setBounds(itemView.getRight() + ((int) dX) - deleteBackgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
                editBackground.draw(c);
                editIcon.draw(c);
            } else { // view is unSwiped
                deleteBackground.setBounds(0, 0, 0, 0);
                editBackground.setBounds(0, 0, 0, 0);
            }

        }
        else if (mAdapter instanceof RecipesAdapter) {
            if (dX < 0) { // Swiping to the left
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);

                deleteBackground.setBounds(itemView.getRight() + ((int) dX) - deleteBackgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                deleteBackground.setBounds(0, 0, 0, 0);
            }
            deleteBackground.draw(c);
            deleteIcon.draw(c);
        }

        /*
        deleteBackground.draw(c);
        deleteIcon.draw(c);
        */
    }

}
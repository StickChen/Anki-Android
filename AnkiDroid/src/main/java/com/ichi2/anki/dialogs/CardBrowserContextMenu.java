
package com.ichi2.anki.dialogs;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichi2.anki.R;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CardBrowserContextMenu extends DialogFragment {
    /**
     * Context Menu items
     */
    public static final int CONTEXT_MENU_MARK = 0;
    public static final int CONTEXT_MENU_SUSPEND = 1;
    public static final int CONTEXT_MENU_DELETE = 2;
    public static final int CONTEXT_MENU_DETAILS = 3;
    public static final int CONTEXT_MENU_FULL_EDIT = 4;
    private static final int MAX_TITLE_LENGTH = 75;

    private static MaterialDialog.ListCallback mContextMenuListener;


    public static CardBrowserContextMenu newInstance(String dialogTitle, boolean isMarked,
            boolean isSuspended, MaterialDialog.ListCallback contextMenuListener) {
        CardBrowserContextMenu f = new CardBrowserContextMenu();
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putBoolean("isMarked", isMarked);
        args.putBoolean("isSuspended", isSuspended);
        mContextMenuListener = contextMenuListener;
        f.setArguments(args);
        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        String[] entries = new String[5];
        entries[CONTEXT_MENU_DELETE] = res.getString(R.string.card_browser_delete_card);
        entries[CONTEXT_MENU_DETAILS] = res.getString(R.string.card_editor_preview_card);
        entries[CONTEXT_MENU_MARK] = res.getString(
                getArguments().getBoolean("isMarked") ?
                        R.string.card_browser_unmark_card :
                        R.string.card_browser_mark_card);
        entries[CONTEXT_MENU_SUSPEND] = res.getString(
                getArguments().getBoolean("isSuspended") ?
                        R.string.card_browser_unsuspend_card :
                        R.string.card_browser_suspend_card);
        entries[CONTEXT_MENU_FULL_EDIT] = "完整编辑";
        // Ellipsize the title if it's obscenely long
        String title = getArguments().getString("dialogTitle");
        if (title != null && title.length() > MAX_TITLE_LENGTH) {
            title = title.substring(0, MAX_TITLE_LENGTH) + "…";
        }
        return new MaterialDialog.Builder(getActivity())
                .title(title)
                .items(entries)
                .itemsCallback(mContextMenuListener)
                .build();
    }

}

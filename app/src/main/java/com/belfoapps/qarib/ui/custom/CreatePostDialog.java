package com.belfoapps.qarib.ui.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.belfoapps.qarib.databinding.ContactsDialogBinding;
import com.belfoapps.qarib.databinding.SocialMediaDialogBinding;
import com.belfoapps.qarib.databinding.WebsiteDialogBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.utils.Types;

public class CreatePostDialog extends DialogFragment {
    private static final String TAG = "PostDialog";

    public interface PostCreationListener {

        void createPost(Post post);

    }

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private PostCreationListener listener;
    private String type;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    public CreatePostDialog(String type) {
        this.type = type;
    }

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (PostCreationListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        Dialog dialog = null;

        switch (Types.valueOf(type)) {
            case Contacts:
                dialog = getContactsDialog(builder, inflater);
                break;
            case SocialMedia:
                dialog = getSocialMediaDialog(builder, inflater);
                break;
            case Website:
                dialog = getWebsiteDialog(builder, inflater);
                break;
            case Location:
                Log.d(TAG, "onCreateDialog: Location thing");
                break;
        }

        //Make Transparent Background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    private Dialog getContactsDialog(AlertDialog.Builder builder, LayoutInflater inflater) {
        Dialog dialog;
        ContactsDialogBinding cBinding = ContactsDialogBinding.inflate(inflater, null,
                false);

        cBinding.createPost.setOnClickListener(v -> {
            if (cBinding.contactsPost.getEditText().getText().toString().equals(""))
                cBinding.contactsPost.setError("Error Post");
            else if (cBinding.contactsContent.getEditText().getText().toString().equals(""))
                cBinding.contactsContent.setError("Error Content");
            else if (cBinding.contactsEmail.getEditText().getText().toString().equals(""))
                cBinding.contactsEmail.setError("Error Email");
            else if (cBinding.contactsPhone.getEditText().getText().toString().equals(""))
                cBinding.contactsPhone.setError("Error Phone");
            else {
                Post post = new Post(null, cBinding.contactsPost.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setContent(cBinding.contactsContent.getEditText().getText().toString());
                post.setEmail(cBinding.contactsEmail.getEditText().getText().toString());
                post.setPhone(cBinding.contactsPhone.getEditText().getText().toString());

                listener.createPost(post);
            }
        });
        dialog = builder.setView(cBinding.getRoot()).create();
        return dialog;
    }

    private Dialog getSocialMediaDialog(AlertDialog.Builder builder, LayoutInflater inflater) {
        Dialog dialog;
        SocialMediaDialogBinding smBinding = SocialMediaDialogBinding.inflate(inflater, null,
                false);
        smBinding.createPost.setOnClickListener(v -> {
            if (smBinding.socialMediaPost.getEditText().getText().toString().equals(""))
                smBinding.socialMediaPost.setError("Error Post");
            else if (smBinding.socialMediaContent.getEditText().getText().toString().equals(""))
                smBinding.socialMediaContent.setError("Error Content");
            else {
                Post post = new Post(null, smBinding.socialMediaPost.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setContent(smBinding.socialMediaContent.getEditText().getText().toString());

                listener.createPost(post);
            }

        });
        dialog = builder.setView(smBinding.getRoot()).create();
        return dialog;
    }

    private Dialog getWebsiteDialog(AlertDialog.Builder builder, LayoutInflater inflater) {
        Dialog dialog;
        WebsiteDialogBinding wBinding = WebsiteDialogBinding.inflate(inflater, null,
                false);
        wBinding.createPost.setOnClickListener(v -> {
            if (wBinding.websitePost.getEditText().getText().toString().equals(""))
                wBinding.websitePost.setError("Error Post");
            else if (wBinding.websiteContent.getEditText().getText().toString().equals(""))
                wBinding.websiteContent.setError("Error Content");
            else if (wBinding.website.getEditText().getText().toString().equals(""))
                wBinding.website.setError("Error Website");
            else {
                Post post = new Post(null, wBinding.websitePost.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setContent(wBinding.websiteContent.getEditText().getText().toString());
                post.setWebsite(wBinding.website.getEditText().getText().toString());
            }
        });
        dialog = builder.setView(wBinding.getRoot()).create();
        return dialog;
    }
}

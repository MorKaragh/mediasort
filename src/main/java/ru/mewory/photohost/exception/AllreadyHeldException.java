package ru.mewory.photohost.exception;

import ru.mewory.photohost.model.Record;
import ru.mewory.photohost.model.socnet.Comment;
import ru.mewory.photohost.model.socnet.CommentStatus;

/**
 * Created by tookuk on 11/16/17.
 */
public class AllreadyHeldException extends Exception {
    private Record record;
    private Comment comment;

    public AllreadyHeldException(Comment comment, Record record) {
        super(comment.getChangeUser());
        this.comment = comment;
        this.record = record;
    }

    public CommentStatus getStatus() {
        return comment.getStatus();
    }

    public Record getRecord() {
        return record;
    }

    public Comment getComment() {
        return comment;
    }
}

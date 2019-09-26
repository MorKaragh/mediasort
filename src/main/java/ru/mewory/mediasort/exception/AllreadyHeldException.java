package ru.mewory.mediasort.exception;

import ru.mewory.mediasort.model.Record;
import ru.mewory.mediasort.model.socnet.Comment;
import ru.mewory.mediasort.model.socnet.CommentStatus;


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

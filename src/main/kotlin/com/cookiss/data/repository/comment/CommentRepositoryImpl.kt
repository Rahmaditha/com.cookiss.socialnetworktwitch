package com.cookiss.data.repository.comment

import com.cookiss.data.models.Comment
import com.cookiss.data.models.Like
import com.cookiss.data.models.Post
import com.cookiss.data.responses.CommentResponse
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CommentRepositoryImpl(
    db: CoroutineDatabase
): CommentRepository {

    private val posts = db.getCollection<Post>()
    private val comments = db.getCollection<Comment>()
    private val likes = db.getCollection<Like>()

    override suspend fun createComment(comment: Comment): String {
        comments.insertOne(comment)
        return comment.id
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        val deleteCount = comments.deleteOneById(commentId).deletedCount
        return deleteCount > 0
    }

    override suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse> {
        return comments.find(Comment::postId eq postId).toList().map { comment ->
            val isLiked = likes.findOne(
                and(
                    Like::userId eq ownUserId,
                    Like::parentId eq comment.id
                )
            ) != null
            CommentResponse(
                id = comment.id,
                username = comment.username,
                profilePictureUrl = comment.profileImageUrl,
                timestamp = comment.timestamp,
                comment = comment.comment,
                isLiked = isLiked,
                likeCount = comment.likeCount
            )
        }
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(commentId)
    }

    override suspend fun deleteCommentsFromPost(postId: String): Boolean {
        return comments.deleteMany(
            Comment::postId eq postId
        ).wasAcknowledged()
    }
}
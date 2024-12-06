import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchVideoDetails, VideoDetails } from '../utils/api';
import '../styles/VideoPage.css';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faThumbsUp, faThumbsDown } from '@fortawesome/free-solid-svg-icons';



interface UserComment {
  commentId: number;
  id: number;
  text: string;
  author: string;
  timestamp: Date;
  likes: number;
  dislikes: number;
}

interface CommentDTO {
  content: string;
  videoId: number;
  author: string;
  timestamp: Date;
  likes: number;
  dislikes: number;
}

interface VideoPageProps {
  username: string | null;
}

const VideoPage: React.FC<VideoPageProps> = ({ username }) => {
  const { id } = useParams<{ id: string }>();
  const [videoDetails, setVideoDetails] = useState<VideoDetails | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [newComment, setNewComment] = useState<string>('');
  const [comments, setComments] = useState<UserComment[]>([]);
  const [userId, setUserId] = useState<number | null>(null);

  // Fetch userId based on username
  useEffect(() => {
    async function loadUserId() {
      if (username) {
        try {
          const response = await fetch(`http://localhost:8080/api/users/${username}`);
          if (!response.ok) {
            throw new Error('Failed to fetch userId');
          }
          const data = await response.json();
          setUserId(data.id);
        } catch (err) {
          console.error('Error fetching userId:', err);
          setError((err as Error).message);
        }
      }
    }

    loadUserId();
  }, [username]);

  // Fetch video details
  useEffect(() => {
    async function loadVideoDetails() {
      try {
        if (!id) throw new Error('Video ID is missing');
        const data = await fetchVideoDetails(Number(id));
        setVideoDetails(data);
        setComments(
          data.meta.comments.map((comment, index) => ({
            id: index,
            commentId: comment.commentId,
            text: comment.text,
            author: comment.author,
            timestamp: new Date(comment.timestamp),
            likes: comment.likes,
            dislikes: comment.dislikes,
          }))

        );
        console.log(data.meta.categories);
        console.log(data.meta.tags);
      } catch (err) {
        setError(`Error loading video: ${(err as Error).message}`);
      } finally {
        setLoading(false);
      }
    }

    loadVideoDetails();
  }, [id]);

  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!newComment.trim()) {
      alert('Comment content cannot be empty.');
      return;
    }

    const commentData: CommentDTO = {
      content: newComment,
      videoId: Number(id),
      author: username || 'Anonymous',
      timestamp: new Date(),
      likes: 0,
      dislikes: 0,
    };

    try {
      const response = await fetch('http://localhost:8080/api/comments/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(commentData),
      });

      if (!response.ok) {
        throw new Error('Failed to post comment');
      }

      setComments((prevComments) => [
        ...prevComments,
        {
          id: prevComments.length + 1,
          commentId: prevComments.length + 1, // Add commentId here
          text: commentData.content,
          author: commentData.author,
          timestamp: commentData.timestamp,
          likes: commentData.likes,
          dislikes: commentData.dislikes,
        },
      ]);
      setNewComment('');
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const handleReaction = async (commentId: number, isLike: boolean) => {
    if (!userId) {
      alert('You need to log in to react to a comment.');
      return;
    }

    try {
      // Send reaction request
      const response = await fetch(
        `http://localhost:8080/api/comments/${commentId}/react?userId=${userId}&isLike=${isLike}`,
        { method: 'POST' }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Failed to react to comment.');
      }

      // Determine response type (JSON or plain text)
      const contentType = response.headers.get('Content-Type');
      let message;
      if (contentType && contentType.includes('application/json')) {
        const jsonResponse = await response.json();
        message = jsonResponse.message || 'Reaction processed successfully.';
      } else {
        message = await response.text();
      }

      console.log('Server response:', message);

      // Fetch updated comment details
      const commentResponse = await fetch(
        `http://localhost:8080/api/comments/${commentId}`
      );

      if (!commentResponse.ok) {
        const errorText = await commentResponse.text();
        console.error('Failed to fetch updated comment details:', errorText);
        throw new Error('Failed to fetch updated comment details.');
      }

      const updatedComment = await commentResponse.json();
      console.log('Updated comment details:', updatedComment);

      // Update local state with new likes/dislikes
      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.commentId === commentId
            ? {
              ...comment,
              likes: updatedComment.likes,
              dislikes: updatedComment.dislikes,
            }
            : comment
        )
      );

      alert(message); // Optional feedback to the user
    } catch (err) {
      console.error('Error handling reaction:', err);
      alert((err as Error).message || 'An unexpected error occurred.');
    }
  };

  const handleLike = (commentId: number) => {
    handleReaction(commentId, true); // Like reaction
  };

  const handleDislike = (commentId: number) => {
    handleReaction(commentId, false); // Dislike reaction
  };


  if (loading) {
    return <div className="loading">Loading video...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!videoDetails) {
    return <div className="error">Video not found</div>;
  }

  return (
    <div className="video-page">
      <div className="video-container">
        <video controls width={videoDetails.width} height={videoDetails.height} src={videoDetails.videoUrl}>
          Your browser does not support the video tag.
        </video>
      </div>
      <div className="video-info">
        <h2>{videoDetails.title}</h2>
        <p>{videoDetails.meta.description}</p>
        <div className="meta-info">
          <p><strong>Uploaded by:</strong> {videoDetails.user}</p>
          <p><strong>Categories:</strong> {videoDetails.meta.categories.join(', ')}</p>
          <p><strong>Tags:</strong> {videoDetails.meta.tags.join(', ')}</p>
        </div>
      </div>

      <div className="comments-section">
        <form onSubmit={handleCommentSubmit} className="comment-form">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Write your comment here..."
            required
          />
          <button type="submit">Post Comment</button>
        </form>

        <h3>Comments:</h3>
        <ul className="comments-list">
          {comments.map((comment) => (
            <li key={comment.id} className="comment-item">
              <p>{comment.text}</p>
              <p>
                <em>
                  by {comment.author} on {new Date(comment.timestamp).toLocaleString()}
                </em>
              </p>
              <p>
                <FontAwesomeIcon
                  icon={faThumbsUp}
                  onClick={() => handleLike(comment.commentId)}
                  className="icon like-icon"
                  title="Like"
                />{' '}
                {comment.likes}
                {' '}
                <FontAwesomeIcon
                  icon={faThumbsDown}
                  onClick={() => handleDislike(comment.commentId)}
                  className="icon dislike-icon"
                  title="Dislike"
                />{' '}
                {comment.dislikes}
              </p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default VideoPage;

import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchVideoDetails, VideoDetails } from '../utils/api';
import '../styles/VideoPage.css';

interface UserComment {
  text: string;
  author: string;
  timestamp: Date,
  likes: number;
  dislikes: number;
}

interface CommentDTO {
  content: string;
  videoId: number;
  author: string;
  timestamp: Date,
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

  useEffect(() => {
    async function loadVideoDetails() {
      try {
        if (!id) throw new Error('Video ID is missing');
        const data = await fetchVideoDetails(Number(id));
        setVideoDetails(data);
        setComments(data.meta.comments);
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
      author: username || 'Anonymous', // Send the username or 'Anonymous' if not logged in
      timestamp: new Date(),
      likes: 0,
      dislikes: 0,
    };

    console.log('Posting comment:', commentData);

    try {
      const response = await fetch('http://localhost:8080/api/comments/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(commentData),
      });

      if (!response.ok) {
        throw new Error('Failed to post comment');
      }

      // Add new comment to the video details
      const updatedComments = [
        ...comments,
        {
          text: commentData.content,
          author: commentData.author,
          timestamp: commentData.timestamp,
          likes: commentData.likes,
          dislikes: commentData.dislikes,
        },
      ];
      setComments(updatedComments);
      setNewComment('');
    } catch (err) {
      setError((err as Error).message);
    }
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
        <video
          controls
          width={videoDetails.width}
          height={videoDetails.height}
          src={videoDetails.videoUrl}
        >
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
        {/* Mover el formulario arriba */}
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
          {comments.map((comment, index) => (
            <li key={index} className="comment-item">
              <p>{comment.text}</p>
              <p>
                <em>
                  by {comment.author} on{' '}
                  {new Date(comment.timestamp).toLocaleString()}
                </em>
              </p>
              <p>Likes: {comment.likes} | Dislikes: {comment.dislikes}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default VideoPage;
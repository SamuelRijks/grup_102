import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchVideoDetails, VideoDetails } from '../utils/api';
import '../styles/VideoPage.css';

const VideoPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [videoDetails, setVideoDetails] = useState<VideoDetails | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    async function loadVideoDetails() {
      try {
        if (!id) throw new Error('Video ID is missing');
        const data = await fetchVideoDetails(Number(id));
        console.log('url:', data.videoUrl); // Mostrar la URL del video en la consola
        console.log('Comments received:', data.meta.comments); // Mostrar comentarios en la consola
        setVideoDetails(data);
      } catch (err) {
        setError(`Error loading video: ${(err as Error).message}`);
      } finally {
        setLoading(false);
      }
    }

    loadVideoDetails();
  }, [id]);

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
          <p><strong>Comments:</strong></p>
          <ul>
            {videoDetails.meta.comments.map((comment, index) => (
              <li key={index}>
                <p>{comment.text}</p>
                <p><em>by {comment.author} on {new Date(comment.timestamp).toLocaleString()}</em></p>
                <p>Likes: {comment.likes} | Dislikes: {comment.dislikes}</p>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default VideoPage;
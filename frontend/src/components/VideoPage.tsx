import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { fetchVideoDetails } from '../utils/api';
import '../styles/VideoPage.css';
const VITE_API_DOMAIN = import.meta.env.VITE_API_DOMAIN;

interface UserComment {
  text: string;
  author: string;
}

interface Meta {
  description: string;
  categories: string[];
  tags: string[];
  comments: UserComment[];
}

interface VideoDetails {
  id: number;
  width: number;
  height: number;
  duration: number;
  title: string;
  user: string;
  meta: Meta;
}

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
          width={videoDetails.width}
          height={videoDetails.height}
        >
          <source
            src={`${VITE_API_DOMAIN}/videos/${videoDetails.id}.mp4`}
            type="video/mp4"
          />
          Your browser does not support the video tag.
        </video>
        <h1 className="video-title">{videoDetails.title}</h1>
        <p className="video-user">Uploaded by: {videoDetails.user}</p>
        <p className="video-duration">
          Duration: {videoDetails.duration.toFixed(1)} seconds
        </p>
        <p className="video-description">{videoDetails.meta.description}</p>
        <p className="video-categories">
          Categories: {videoDetails.meta.categories.join(', ') || 'None'}
        </p>

        <div className="comments-section">
          <h2>Comments</h2>
          {videoDetails.meta.comments.length > 0 ? (
            videoDetails.meta.comments.map((comment, index) => (
              <div key={index} className="comment">
                <strong>{comment.author}:</strong> {comment.text}
              </div>
            ))
          ) : (
            <p>No comments available.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default VideoPage;

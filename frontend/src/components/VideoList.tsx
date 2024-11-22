import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Video } from '../utils/api';
import '../styles/VideoList.css';

interface VideoListProps {
  videos: Video[];
}

const VideoList: React.FC<VideoListProps> = ({ videos }) => {
  const navigate = useNavigate();

  const handleVideoClick = (videoId: number) => {
    navigate(`/video/${videoId}`);
  };

  return (
    <div className="video-list">
      {videos.map((video) => (
        <div
          key={video.id}
          className="video-item"
          onClick={() => handleVideoClick(video.id)}
        >
          <img src={video.thumbnail} alt={video.title} />
          <h3>{video.title}</h3>
          <p>{video.user}</p>
        </div>
      ))}
    </div>
  );
};

export default VideoList;
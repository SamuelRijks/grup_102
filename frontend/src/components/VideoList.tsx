import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Video } from '../utils/api';
import '../styles/VideoList.css';

interface VideoListProps {
  videos: Video[];
}

const VideoList: React.FC<VideoListProps> = ({ videos }) => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState<string>('');

  const handleVideoClick = (videoId: number) => {
    navigate(`/video/${videoId}`);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value.toLowerCase());
  };

  const filteredVideos = videos.filter((video) =>
    video.title.toLowerCase().includes(searchQuery)
  );

  return (
    <div className="app-container">
      {/* Search Bar */}
      <div className="search-bar-container">
        <input
          type="text"
          placeholder="Search videos..."
          className="search-input"
          value={searchQuery}
          onChange={handleSearchChange}
        />
      </div>
      {/* Video List */}
      <div className="video-list">
        {filteredVideos.length > 0 ? (
          filteredVideos.map((video) => (
            <div
              key={video.id}
              className="video-item"
              onClick={() => handleVideoClick(video.id)}
            >
              <img src={video.thumbnail} alt={video.title} />
              <h3>{video.title}</h3>
              <p>{video.user}</p>
            </div>
          ))
        ) : (
          <p className="no-results">No videos found.</p>
        )}
      </div>
    </div>
  );
};

export default VideoList;

import React from 'react';
import { Video } from '../utils/api';
import '../styles/VideoList.css';

interface VideoListProps {
    videos: Video[];
    onVideoSelect: (video: Video) => void;
}

const VideoList: React.FC<VideoListProps> = ({ videos, onVideoSelect }) => {
    return (
        <div className="video-list">
            {videos.map((video) => (
                <div
                    key={video.id}
                    className="video-item"
                    onClick={() => onVideoSelect(video)}
                >
                    <img
                        src={video.thumbnail}
                        alt={video.title}
                    />
                    <h3>{video.title}</h3>
                    <p>{video.user}</p>
                </div>
            ))}
        </div>
    );
};

export default VideoList;
import React from 'react';
import { Video } from '../utils/api';
import '../styles/VideoPlayer.css';

interface VideoPlayerProps {
    video: Video;
    onClose: () => void;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ video, onClose }) => {
    return (
        <div className="video-player">
            <button className="close-button" onClick={onClose}>×</button>
            <h2>{video.title}</h2>
            <video controls style={{ width: '80%', maxWidth: '900px', height: 'auto', borderRadius: '8px' }}>
                <source src={`${import.meta.env.VITE_API_DOMAIN}/media/${video.id}.mp4`} type="video/mp4" />
                Your browser does not support the video tag.
            </video>
            <div className="progress-bar">
                <div className="progress" style={{ width: '50%' }}></div>
            </div>
        </div>
    );
};

export default VideoPlayer;

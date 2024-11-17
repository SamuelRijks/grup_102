import React from 'react';
import { useNavigate } from 'react-router-dom';
import { VideoDetails } from '../utils/api';
import '../styles/VideoPlayer.css';

interface VideoPlayerProps {
    videoDetails: VideoDetails;
    onClose: () => void;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ videoDetails, onClose }) => {
    const navigate = useNavigate();

    const handleClose = () => {
        onClose();
        navigate('/');
    };

    return (
        <div className="video-player">
            <button className="close-button" onClick={handleClose}>×</button>
            <h2>{videoDetails.title}</h2>
            <p>{videoDetails.description}</p>
            <video controls style={{ width: '80%', maxWidth: '900px', height: 'auto', borderRadius: '8px' }}>
                <source src={videoDetails.videoUrl} type="video/mp4" />
                Your browser does not support the video tag.
            </video>
        </div>
    );
};

export default VideoPlayer;
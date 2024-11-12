import React from 'react';
import { Video } from '../utils/api';

interface VideoListProps {
    videos: Video[];
}

const VideoList: React.FC<VideoListProps> = ({ videos }) => {
    return (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px' }}>
            {videos.map((video) => (
                <div key={video.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '8px', textAlign: 'center' }}>
                    <img
                        src={video.thumbnail}
                        alt={video.title}
                        width="100%"
                        height="auto"
                        style={{ borderRadius: '8px', marginBottom: '8px' }}
                    />
                    <h3 style={{ fontSize: '1rem', margin: '8px 0' }}>{video.title}</h3>
                    <p style={{ fontSize: '0.9rem', color: '#555' }}>{video.user}</p>
                </div>
            ))}
        </div>
    );
};

export default VideoList;
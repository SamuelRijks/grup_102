import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MyVideosPage.css';

interface Video {
    id: number;
    title: string;
    thumbnail: string;
}

interface MyVideosPageProps {
    username: string | null;
}

const MyVideosPage: React.FC<MyVideosPageProps> = ({ username }) => {
    const [videos, setVideos] = useState<Video[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        if (username) {
            async function fetchVideos() {
                try {
                    const response = await fetch(`http://localhost:8080/api/users/${username}/videos`);
                    if (!response.ok) {
                        throw new Error('Failed to fetch videos');
                    }
                    const data = await response.json();
                    setVideos(data);
                } catch (err) {
                    setError((err as Error).message);
                } finally {
                    setLoading(false);
                }
            }
            fetchVideos();
        }
    }, [username]);

    if (!username) {
        return <p className="centered-message">Please log in to see your videos.</p>;
    }

    if (loading) {
        return <p className="centered-message">Loading videos...</p>;
    }

    if (error) {
        return <p className="centered-message error">{error}</p>;
    }

    if (videos.length === 0) {
        return <p className="centered-message">You haven't posted any videos yet.</p>;
    }

    return (
        <div className="my-videos-page">
            <h2>My Videos</h2>
            <ul>
                {videos.map((video) => (
                    <li key={video.id}>
                        <Link to={`/video/${video.id}`}>
                            <img src={`${import.meta.env.VITE_API_DOMAIN}/api/images/${video.id}.webp`} alt={video.title} />
                            <p>{video.title}</p>
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MyVideosPage;
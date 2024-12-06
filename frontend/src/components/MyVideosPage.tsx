import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MyVideosPage.css';
import EditVideo from './EditVideo';

interface Video {
    id: number;
    title: string;
    thumbnail: string;
}

interface Category {
    id: number;
    name: string;
}

interface MyVideosPageProps {
    username: string | null;
}

const MyVideosPage: React.FC<MyVideosPageProps> = ({ username }) => {
    const [videos, setVideos] = useState<Video[]>([]);
    const [userId, setUserId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [title, setTitle] = useState<string>('');
    const [file, setFile] = useState<File | null>(null);
    const [uploading, setUploading] = useState<boolean>(false);
    const [categories, setCategories] = useState<Category[]>([]);
    const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
    const [tags, setTags] = useState<string>('');
    const [newTag, setNewTag] = useState<string>('');
    const [editingVideoId, setEditingVideoId] = useState<number | null>(null);

    const fetchUserVideos = async () => {
        try {
            const token = localStorage.getItem('authToken');
            console.log('Token being sent:', token); // Debug: Log the token being sent
    
            if (!token) {
                throw new Error('Authentication token is missing');
            }
    
            const response = await fetch(`http://localhost:8080/api/users/${userId}/videos`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
    
            if (!response.ok) {
                if (response.status === 403) {
                    throw new Error('Access denied');
                } else if (response.status === 404) {
                    throw new Error('User not found');
                } else {
                    throw new Error(`Error: ${response.statusText}`);
                }
            }
    
            const data = await response.json();
            setVideos(data);
        } catch (err) {
            console.error('Error fetching user videos:', err);
            setError(err.message);
        }
    };
    
    

    const [editingVideoId, setEditingVideoId] = useState<number | null>(null);

    const fetchUserVideos = async () => {
        try {
            const token = localStorage.getItem('authToken');
            console.log('Token being sent:', token); // Debug: Log the token being sent
    
            if (!token) {
                throw new Error('Authentication token is missing');
            }
    
            const response = await fetch(`http://localhost:8080/api/users/${userId}/videos`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
    
            if (!response.ok) {
                if (response.status === 403) {
                    throw new Error('Access denied');
                } else if (response.status === 404) {
                    throw new Error('User not found');
                } else {
                    throw new Error(`Error: ${response.statusText}`);
                }
            }
    
            const data = await response.json();
            setVideos(data);
        } catch (err) {
            console.error('Error fetching user videos:', err);
            setError(err.message);
        }
    };
    
    

    useEffect(() => {
        const fetchCategories = async () => {
        const fetchCategories = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/categories');
                if (!response.ok) {
                    throw new Error('Failed to fetch categories');
                }
                const data: Category[] = await response.json();
                setCategories(data);
            } catch (err) {
                setError((err as Error).message);
            }
        };

        };

        fetchCategories();
    }, []);

    useEffect(() => {
        if (username) {
            const fetchUserId = async () => {
            const fetchUserId = async () => {
                try {
                    const response = await fetch(`http://localhost:8080/api/users/${username}`);
                    if (!response.ok) {
                        throw new Error('Failed to fetch user details');
                    }
                    const data = await response.json();
                    setUserId(data.id);
                } catch (err) {
                    setError((err as Error).message);
                }
            };

            };

            fetchUserId();
        }
    }, [username]);

    useEffect(() => {
        if (userId) {
            fetchUserVideos();
        }
    }, [userId]);

    useEffect(() => {
        if (userId) {
            fetchUserVideos();
        }
    }, [userId]);

    const handleCategorySelect = (id: number) => {
        setSelectedCategories((prev) =>
            prev.includes(id) ? prev.filter((catId) => catId !== id) : [...prev, id]
        );
    };

    const handleAddTag = () => {
        if (newTag.trim()) {
            setTags((prev) => (prev ? `${prev},${newTag}` : newTag));
            setNewTag('');
        }
    };

    const handleVideoUpdated = () => {
        fetchUserVideos(); // Reusing fetchUserVideos for video updates
    };

    const handleVideoUpdated = () => {
        fetchUserVideos(); // Reusing fetchUserVideos for video updates
    };

    const uploadVideo = async () => {
        if (!title || !file || !userId) {
            setError('Title, MP4 file, and user ID are required');
            return;
        }

        setUploading(true);
        setError(null);

        try {
            const formData = new FormData();
            formData.append('title', title);
            formData.append('file', file);
            formData.append('userId', userId.toString());

            const categoryNames = categories
                .filter((category) => selectedCategories.includes(category.id))
                .map((category) => category.name);

            formData.append('categories', JSON.stringify(categoryNames));
            formData.append('tags', tags);

            const response = await fetch('http://localhost:8080/api/videos/upload', {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) {
                throw new Error('Failed to upload video');
            }

            const newVideo = await response.json();

            // Add new video to the state
            if (newVideo.videoId) {
                setVideos((prevVideos) => [
                    ...prevVideos,
                    {
                        id: newVideo.videoId,
                        title: title,
                        thumbnail: `/api/images/${newVideo.videoId}.webp`,
                    },
                ]);
            }

            // Reset form fields

            // Add new video to the state
            if (newVideo.videoId) {
                setVideos((prevVideos) => [
                    ...prevVideos,
                    {
                        id: newVideo.videoId,
                        title: title,
                        thumbnail: `/api/images/${newVideo.videoId}.webp`,
                    },
                ]);
            }

            // Reset form fields
            setTitle('');
            setFile(null);
            setSelectedCategories([]);
            setTags('');
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setUploading(false);
        }
    };

    // Log videos for debugging
    useEffect(() => {
        console.log('Videos:', videos);
    }, [videos]);

    useEffect(() => {
        if (username) {
            async function fetchUserVideos() {
                try {
                    const response = await fetch(`http://localhost:8080/api/users/${username}/videos`);
                    if (!response.ok) {
                        throw new Error('Failed to fetch videos');
                    }
                    const data: Video[] = await response.json();
                    setVideos(data);
                } catch (err) {
                    setError((err as Error).message);
                } finally {
                    setLoading(false);
                }
            }
            fetchUserVideos();
        }
    }, [username]);


    return (
        <div className="my-videos-page">
            <h2>My Videos</h2>
            <div className="upload-video-form">
                <h3>Upload a New Video</h3>
                <input
                    type="text"
                    placeholder="Video Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
                <input
                    type="file"
                    accept="video/mp4"
                    onChange={(e) => setFile(e.target.files?.[0] || null)}
                />
                <div className="categories-section">
                    <h4>Select Categories</h4>
                    <div className="categories-list">
                        {categories.map((category) => (
                            <label key={category.id}>
                                <input
                                    type="checkbox"
                                    checked={selectedCategories.includes(category.id)}
                                    onChange={() => handleCategorySelect(category.id)}
                                />
                                {category.name}
                            </label>
                        ))}
                    </div>
                </div>
                <div className="tags-section">
                    <h4>Tags</h4>
                    <div className="tags-input">
                        <input
                            type="text"
                            placeholder="Enter tag"
                            value={newTag}
                            onChange={(e) => setNewTag(e.target.value)}
                        />
                        <button type="button" onClick={handleAddTag}>
                            Add Tag
                        </button>
                    </div>
                    <p>Current Tags: {tags}</p>
                </div>
                <button onClick={uploadVideo} disabled={uploading}>
                    {uploading ? 'Uploading...' : 'Upload Video'}
                </button>
                {error && <p className="error">{error}</p>}
            </div>
            {loading ? (
                <p>Loading videos...</p>
            ) : (
                <ul>
                    {videos.map((video) => (
                        <li key={video.id}>
                            <Link to={`/video/${video.id}`}>
                                <img
                                    src={`http://localhost:8080/api/images/${video.id}.webp`}
                                    alt={video.title}
                                />
                                <p>{video.title}</p>
                            </Link>
                        </li>
                    ))}
            {loading ? (
                <p>Loading videos...</p>
            ) : (
                <ul>
                    {videos.map((video) => (
                        <li key={video.id}>
                            <Link to={`/video/${video.id}`}>
                                <img
                                    src={`http://localhost:8080/api/images/${video.id}.webp`}
                                    alt={video.title}
                                />
                                <p>{video.title}</p>
                            </Link>
                        </li>
                    ))}
                </ul>
            )}
            {editingVideoId && (
                <EditVideo
                    videoId={editingVideoId}
                    onClose={() => setEditingVideoId(null)}
                    onVideoUpdated={handleVideoUpdated}
                />
            )}
            )}
            {editingVideoId && (
                <EditVideo
                    videoId={editingVideoId}
                    onClose={() => setEditingVideoId(null)}
                    onVideoUpdated={handleVideoUpdated}
                />
            )}
        </div>
    );
};

export default MyVideosPage;
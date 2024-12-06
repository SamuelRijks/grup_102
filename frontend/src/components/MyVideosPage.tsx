import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MyVideosPage.css';
import EditVideo from './EditVideo';

interface Category {
    id: number;
    name: string;
}

interface Video {
    id: number;
    title: string;
    thumbnail: string;
}

interface MyVideosPageProps {
    username: string | null;
}

const MyVideosPage: React.FC<MyVideosPageProps> = ({ username }) => {
    const [title, setTitle] = useState<string>('');
    const [file, setFile] = useState<File | null>(null);
    const [tags, setTags] = useState<string>('');
    const [newTag, setNewTag] = useState<string>('');
    const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [videos, setVideos] = useState<Video[]>([]);
    const [uploading, setUploading] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [editingVideoId, setEditingVideoId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // Fetch available categories
        async function fetchCategories() {
            try {
                const response = await fetch('http://localhost:8080/api/categories');
                const data = await response.json();
                setCategories(data);
            } catch (err) {
                setError('Failed to fetch categories');
            }
        }

        // Fetch user's videos
        async function fetchVideos() {
            try {
                const response = await fetch(`http://localhost:8080/api/users/${username}/videos`);
                const data = await response.json();
                setVideos(data);
            } catch (err) {
                setError('Failed to fetch videos');
            } finally {
                setLoading(false);
            }
        }

        fetchCategories();
        fetchVideos();
    }, [username]);

    const handleCategorySelect = (id: number) => {
        setSelectedCategories((prev) =>
            prev.includes(id) ? prev.filter((catId) => catId !== id) : [...prev, id]
        );
    };

    const handleAddTag = () => {
        if (newTag.trim()) {
            setTags((prev) => (prev ? `${prev}, ${newTag}` : newTag));
            setNewTag('');
        }
    };

    const uploadVideo = async () => {
        if (!title || !file) {
            setError('Title and file are required');
            return;
        }
        setUploading(true);
        setError(null);

        try {
            const formData = new FormData();
            formData.append('title', title);
            formData.append('file', file);
            formData.append('categories', JSON.stringify(selectedCategories));
            formData.append('tags', tags);

            const response = await fetch('http://localhost:8080/api/videos/upload', {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Failed to upload video');

            const newVideo = await response.json();
            setVideos((prev) => [...prev, { id: newVideo.id, title, thumbnail: newVideo.thumbnail }]);
            setTitle('');
            setFile(null);
            setSelectedCategories([]);
            setTags('');
        } catch (err) {
            setError('Failed to upload video');
        } finally {
            setUploading(false);
        }
    };

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
                <div className="file-upload-section">
                    <h4>Upload File</h4>
                    <input
                        type="file"
                        accept="video/mp4"
                        id="file-upload"
                        style={{ display: 'none' }}
                        onChange={(e) => setFile(e.target.files?.[0] || null)}
                    />
                    <label htmlFor="file-upload" className="button-primary">
                        Select File
                    </label>
                    {file && <p className="file-selected">Selected File: {file.name}</p>}
                </div>
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
                        <button className="button-primary  add-tag-button" onClick={handleAddTag}>
                            Add Tag
                        </button>
                    </div>
                    <p>Current Tags: {tags}</p>
                </div>
                <button className="button-primary" onClick={uploadVideo} disabled={uploading}>
                    {uploading ? 'Uploading...' : 'Upload Video'}
                </button>
                {error && <p className="error">{error}</p>}
            </div>
            {loading ? (
                <p>Loading videos...</p>
            ) : (
                <div className="video-list">
                    {videos.map((video) => (
                        <div key={video.id} className="video-item">
                            <Link to={`/video/${video.id}`}>
                                <img src={video.thumbnail} alt={video.title} />
                                <p>{video.title}</p>
                            </Link>
                            <button className="button-primary" onClick={() => setEditingVideoId(video.id)}>
                                Edit
                            </button>
                        </div>
                    ))}
                </div>
            )}
            {editingVideoId && (
                <EditVideo
                    videoId={editingVideoId}
                    username={username}
                    onClose={() => setEditingVideoId(null)}
                    onVideoUpdated={() => setEditingVideoId(null)}
                />
            )}
        </div>
    );
};

export default MyVideosPage;

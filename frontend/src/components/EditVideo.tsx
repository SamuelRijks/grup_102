import React, { useState, useEffect } from 'react';
import '../styles/EditVideo.css';

interface Category {
    id: number;
    name: string;
}

interface EditVideoProps {
    videoId: number;
    username: string | null;
    onClose: () => void;
    onVideoUpdated: () => void;
}

const EditVideo: React.FC<EditVideoProps> = ({ videoId, username, onClose, onVideoUpdated }) => {
    const [title, setTitle] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [categories, setCategories] = useState<Category[]>([]);
    const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
    const [tags, setTags] = useState<string>('');
    const [newTag, setNewTag] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [isOwner, setIsOwner] = useState<boolean>(false);

    useEffect(() => {
        // Fetch video details
        async function fetchVideoDetails() {
            try {
                const response = await fetch(`http://localhost:8080/api/videos/${videoId}/details`);
                if (!response.ok) throw new Error('Failed to load video details');
                const data = await response.json();
                setTitle(data.title);
                setDescription(data.description || '');
                setSelectedCategories(data.categories.map((category: Category) => category.id));
                setTags(data.tags.map((tag: { name: string }) => tag.name).join(', '));
                setIsOwner(data.uploaderUsername === username);
            } catch (err) {
                setError('Failed to load video details');
            }
        }
        fetchVideoDetails();
    }, [videoId, username]);

    useEffect(() => {
        // Fetch all categories
        async function fetchCategories() {
            try {
                const response = await fetch('http://localhost:8080/api/categories');
                if (!response.ok) throw new Error('Failed to fetch categories');
                const data: Category[] = await response.json();
                setCategories(data);
            } catch (err) {
                setError('Failed to fetch categories');
            }
        }
        fetchCategories();
    }, []);

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

    const handleSave = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/videos/edit/${videoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    title,
                    description,
                    username,
                    categories: selectedCategories,
                    tags: tags.split(',').map((tag) => tag.trim()),
                }),
            });
            if (!response.ok) throw new Error('Failed to update video');
            onVideoUpdated();
            onClose();
        } catch (err) {
            setError('Failed to save changes');
        }
    };

    if (!isOwner) {
        return (
            <div className="edit-video-modal">
                <div className="edit-video-content">
                    <h2>Access Denied</h2>
                    <p>You are not allowed to edit this video.</p>
                    <button onClick={onClose}>Close</button>
                </div>
            </div>
        );
    }

    return (
        <div className="edit-video-modal">
            <div className="edit-video-content">
                <h2>Edit Video</h2>
                {error && <p className="error">{error}</p>}
                <label>
                    Title:
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </label>
                <label>
                    Description:
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    ></textarea>
                </label>
                <div className="categories-section">
                    <h4>Categories</h4>
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
                            placeholder="Enter new tag"
                            value={newTag}
                            onChange={(e) => setNewTag(e.target.value)}
                        />
                        <button type="button" onClick={handleAddTag}>
                            Add Tag
                        </button>
                    </div>
                    <p>Current Tags: {tags}</p>
                </div>
                <button onClick={handleSave}>Save Changes</button>
                <button onClick={onClose} className="cancel-button">
                    Cancel
                </button>
            </div>
        </div>
    );
};

export default EditVideo;

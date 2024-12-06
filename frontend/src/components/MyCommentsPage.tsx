import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MyCommentsPage.css';

interface Comment {
    id: number;
    content: string;
    videoTitle: string;
    videoId: number;
    userId: number; // Add userId to the Comment interface
}

interface MyCommentsPageProps {
    username: string | null;
}

const MyCommentsPage: React.FC<MyCommentsPageProps> = ({ username }) => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
    const [newContent, setNewContent] = useState<string>('');

    useEffect(() => {
        if (username) {
            async function fetchComments() {
                try {
                    const response = await fetch(`http://localhost:8080/api/users/${username}/comments`);
                    if (!response.ok) {
                        throw new Error('Failed to fetch comments');
                    }
                    const data = await response.json();
                    console.log('Comments received:', data);

                    // Transform the data to the desired format
                    const transformedComments = data.map((comment: any) => ({
                        id: comment.id,
                        content: comment.content,
                        videoTitle: comment.videoTitle,
                        videoId: comment.videoId,
                        userId: comment.userId,
                    }));

                    setComments(transformedComments);
                } catch (err) {
                    setError((err as Error).message);
                } finally {
                    setLoading(false);
                }
            }
            fetchComments();
        }
    }, [username]);

    const handleEditClick = (commentId: number, currentContent: string) => {
        setEditingCommentId(commentId);
        setNewContent(currentContent);
    };

    const handleUpdateComment = async (commentId: number) => {
        const comment = comments.find(comment => comment.id === commentId);
        if (!comment) {
            setError('Comment not found');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/comments/update/${commentId}?userId=${comment.userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'text/plain',
                },
                body: newContent,
            });

            if (!response.ok) {
                throw new Error('Failed to update comment');
            }

            // Update the comment in the state
            setComments(comments.map(comment =>
                comment.id === commentId ? { ...comment, content: newContent } : comment
            ));
            setEditingCommentId(null);
            setNewContent('');
        } catch (err) {
            setError((err as Error).message);
        }
    };

    const handleDeleteComment = async (commentId: number) => {
        const comment = comments.find(comment => comment.id === commentId);
        if (!comment) {
            setError('Comment not found');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/comments/delete/${commentId}?userId=${comment.userId}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Failed to delete comment');
            }

            // Remove the comment from the state
            setComments(comments.filter(comment => comment.id !== commentId));
        } catch (err) {
            setError((err as Error).message);
        }
    };

    if (!username) {
        return <p className="centered-message">Please log in to see your comments.</p>;
    }

    if (loading) {
        return <p className="centered-message">Loading comments...</p>;
    }

    if (error) {
        return <p className="centered-message error">{error}</p>;
    }

    if (comments.length === 0) {
        return <p className="centered-message">You haven't posted any comments yet.</p>;
    }

    return (
        <div className="my-comments-page">
            <h2>My Comments</h2>
            <ul>
                {comments.map((comment) => (
                    <li key={comment.id}>
                        {editingCommentId === comment.id ? (
                            <div>
                                <textarea
                                    value={newContent}
                                    onChange={(e) => setNewContent(e.target.value)}
                                />
                                <button onClick={() => handleUpdateComment(comment.id)}>Save</button>
                                <button onClick={() => setEditingCommentId(null)}>Cancel</button>
                            </div>
                        ) : (
                            <div>
                                <p>{comment.content}</p>
                                <p>Posted on: <Link to={`/video/${comment.videoId}`}>{comment.videoTitle}</Link></p>
                                <button onClick={() => handleEditClick(comment.id, comment.content)}>Edit</button>
                                <button onClick={() => handleDeleteComment(comment.id)}>Delete</button>
                            </div>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MyCommentsPage;
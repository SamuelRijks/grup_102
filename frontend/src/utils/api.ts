// utils/api.ts

const VITE_API_DOMAIN = import.meta.env.VITE_API_DOMAIN;

if (!VITE_API_DOMAIN) {
  throw new Error('VITE_API_DOMAIN is not defined');
}

export interface Video {
  id: number;
  title: string;
  user: string;
  thumbnail: string;
}

interface Category {
  name: string;
}

interface Tag {
  name: string;
}

interface UserComment {
  text: string;
  author: string;
  timestamp: Date;
  likes: number;
  dislikes: number;
}

interface Meta {
  description: string;
  categories: string[];
  tags: string[];
  comments: UserComment[];
}

export interface VideoDetails {
  id: number;
  width: number;
  height: number;
  duration: number;
  title: string;
  user: string;
  videoUrl: string;
  meta: Meta;
}

export async function fetchVideos(): Promise<Video[]> {
  try {
    const response = await fetch(`${VITE_API_DOMAIN}/api/videos/summaries`);
    if (!response.ok) {
      throw new Error(`Failed to fetch video summaries: ${response.statusText}`);
    }
    const videoSummaries: VideoSummaryDTO[] = await response.json();

    return videoSummaries.map(video => ({
      id: video.id,
      title: video.title,
      user: video.uploaderUsername,
      thumbnail: `${VITE_API_DOMAIN}/api/images/${video.id}.webp`,
    })).sort((a, b) => a.id - b.id);
  } catch (error) {
    console.error('Error fetching video summaries:', error);
    throw error;
  }
}

export async function fetchVideoDetails(id: number): Promise<VideoDetails> {
  try {
    const response = await fetch(`${VITE_API_DOMAIN}/api/videos/${id}/details`);
    if (!response.ok) {
      throw new Error(`Failed to fetch video details: ${response.statusText}`);
    }
    const videoDetails: VideoDetailsDTO = await response.json();

    return {
      id: videoDetails.id,
      width: videoDetails.width,
      height: videoDetails.height,
      duration: videoDetails.duration,
      title: videoDetails.title,
      user: videoDetails.uploaderUsername,
      videoUrl: `${VITE_API_DOMAIN}/api/videos/${id}.mp4`,
      meta: {
        description: videoDetails.description || '',
        categories: videoDetails.categories ? videoDetails.categories.map((category: Category) => category.name) : [],
        tags: videoDetails.tags ? videoDetails.tags.map((tag: Tag) => tag.name) : [],
        comments: videoDetails.comments ? videoDetails.comments.map((comment: CommentDTO) => ({
          text: comment.content,
          author: comment.author,
          timestamp: new Date(Date.UTC(...(comment.timestamp as [number, number, number, number, number, number]))), // Convertir array a Date
          likes: comment.likes,
          dislikes: comment.dislikes,
        })) : [],
      },
    };
  } catch (error) {
    console.error(`Error fetching video details for video ID ${id}:`, error);
    throw error;
  }
  
}

interface VideoSummaryDTO {
  id: number;
  title: string;
  uploaderUsername: string;
  thumbnailUrl: string;
}

interface VideoDetailsDTO {
  id: number;
  width: number;
  height: number;
  duration: number;
  title: string;
  uploaderUsername: string;
  description: string;
  categories: Category[];
  tags: Tag[];
  comments: CommentDTO[];
  videoUrl: string;
}

interface CommentDTO {
  content: string;
  author: string;
  timestamp: number[];
  likes: number;
  dislikes: number;
}

/*export async function uploadVideo(videoData: { title: string; file: File; userId: number }): Promise<Video> {
  const formData = new FormData();

  // Populate the FormData object
  formData.append('title', videoData.title);
  formData.append('file', videoData.file);
   formData.append('userId', videoData.userId.toString()); 

  try {
    // Make the API request
    const response = await fetch(`${VITE_API_DOMAIN}/api/videos/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const errorText = await response.text(); // Capture raw error response
      console.error('Upload Error Raw Response:', errorText);
      throw new Error(errorText);
    }

    const createdVideo = await response.json();

    // Ensure the created video conforms to the Video interface
    return {
      id: createdVideo.id,
      title: createdVideo.title,
      user: createdVideo.user || 'Unknown User', // Fallback for user
      thumbnail: `${VITE_API_DOMAIN}/api/images/${createdVideo.id}.webp`,
    };
  } catch (error) {
    console.error('Error uploading video:', error);
    throw error; // Rethrow the error to the caller
  }
}*/
export async function uploadVideo(videoData: { title: string; file: File; userId: number }): Promise<Video> {
  console.log('uploadVideo called with:', videoData); // Log input parameters

  const formData = new FormData();
  formData.append('title', videoData.title);
  formData.append('file', videoData.file);
  formData.append('userId', videoData.userId.toString());

  try {
      console.log('Sending request to API'); // Log before sending the request
      const response = await fetch(`${VITE_API_DOMAIN}/api/videos/upload`, {
          method: 'POST',
          body: formData,
      });

      console.log('Received response from API'); // Log after the response is received

      if (!response.ok) {
          const errorText = await response.text();
          console.error('Upload Error Raw Response:', errorText);
          throw new Error(errorText);
      }

      const createdVideo = await response.json();
      console.log('API Response:', createdVideo); // Log the parsed response
      return {
          id: createdVideo.id,
          title: createdVideo.title,
          user: createdVideo.user || 'Unknown User',
          thumbnail: `${VITE_API_DOMAIN}/api/images/${createdVideo.id}.webp`,
      };
  } catch (error) {
      console.error('Error uploading video:', error);
      throw error; // Propagate the error
  }
}

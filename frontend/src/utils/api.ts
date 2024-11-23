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

interface UserComment {
    text: string;
    author: string;
  }
  
  interface Meta {
    description: string;
    categories: string[];
    tags: string[];
    comments: UserComment[];
  }
  
  interface VideoDetails {
    id: number;
    width: number;
    height: number;
    duration: number;
    title: string;
    user: string;
    meta: Meta;
  }
  

export async function fetchVideos(): Promise<Video[]> {
  console.log('fetchVideos called');
  try {
    const response = await fetch(`${VITE_API_DOMAIN}/api/videos/summaries`);
    if (!response.ok) {
      throw new Error(`Failed to fetch video summaries: ${response.statusText}`);
    }

    const videoSummaries: VideoSummaryDTO[] = await response.json();
    console.log('Fetched data:', videoSummaries);

    return videoSummaries.map((video) => ({
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
    console.log(`fetchVideoDetails called with ID: ${id}`);
    try {
      const response = await fetch(`${VITE_API_DOMAIN}/api/videos/${id}/details`);
      if (!response.ok) {
        throw new Error(`Failed to fetch video details: ${response.statusText}`);
      }
  
      const videoDetails = await response.json();
      console.log('Fetched video details:', videoDetails);
  
      // Return the correctly mapped data structure
      return {
        id: videoDetails.id,
        width: videoDetails.width || 0,
        height: videoDetails.height || 0,
        duration: videoDetails.duration || 0,
        title: videoDetails.title || 'Untitled',
        user: videoDetails.uploaderUsername || 'Unknown',
        meta: {
          description: videoDetails.description || 'No description available',
          categories: videoDetails.categories || [],
          tags: videoDetails.tags || [],
          comments: videoDetails.comments.map((comment: any) => ({
            text: comment.content || 'No content',
            author: comment.author || 'Anonymous',
          })),
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

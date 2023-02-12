export type CategoryType = 'type' | 'model' | 'hashtag';

export interface IHashTag {
  id: string;
  category: CategoryType;
  tag: string;
}

export interface IImage {
  postId: number;
  imageUrl: string;
}

export interface IFollows {
  nickname: string;
}

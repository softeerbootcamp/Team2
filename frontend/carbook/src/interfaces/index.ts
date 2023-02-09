export type CategoryType = 'type' | 'model' | 'hashtag';

export interface IHashTag {
  id: string;
  category: CategoryType;
  tag: string;
}

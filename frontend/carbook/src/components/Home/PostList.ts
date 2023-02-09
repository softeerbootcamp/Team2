import { Component } from '@/core';

export default class PostList extends Component {
  template(): string {
    const { postList } = this.props;

    return `
     ${postList
       .map(
         (post: any) => ` <div class="main__gallery--image" data=${post}></div>`
       )
       .join('')}
    `;
  }
}

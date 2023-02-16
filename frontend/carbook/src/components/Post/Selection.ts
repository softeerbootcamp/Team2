import { Component } from '@/core';
import { qs } from '@/utils';

export default class Selection extends Component {
  setup(): void {
    this.state = this.props;
  }
  template(): string {
    const { label, options, selected } = this.state;

    return `
      <label><span>*</span>${label}</label>
      <select name="type" class="select"  >
          <option>선택하세요</option>
        ${options.map(
          (option: string) =>
            `
          <option value="${option}" ${this.setSelected(
              selected,
              option
            )}>${option}</option>`
        )}
      </select>
    `;
  }

  setEvent(): void {
    this.onHandleSelection();
  }

  onHandleSelection() {
    const selection = qs(this.$target, 'select') as HTMLSelectElement;
    selection.addEventListener('change', () => {
      const selected = selection.options[selection.selectedIndex].text;
      this.props.setFormData(selected);
    });
  }

  setSelected(selected: string, option: string) {
    return selected === option ? 'selected' : '';
  }
}

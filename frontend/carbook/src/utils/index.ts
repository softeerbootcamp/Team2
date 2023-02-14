export function getClosest(target: HTMLElement, name: string) {
  return target.closest(name) as HTMLElement;
}

export function qs(target: HTMLElement | Document, className: string) {
  return target.querySelector(className) as HTMLElement;
}

export function qsa(target: HTMLElement | Document, className: string) {
  return target.querySelectorAll(className) as NodeList;
}

export function isEmptyObj(obj: object) {
  if (obj.constructor === Object && Object.keys(obj).length === 0) {
    return true;
  }

  return false;
}

export function isSameObj(obj1: object, obj2: object) {
  return Object.entries(obj1).toString() === Object.entries(obj2).toString();
}

export function getObjectKeyArray(object: object) {
  return Object.entries(object).map(([key, _]) => key);
}

export function onChangeInputHandler(
  input: HTMLInputElement | HTMLTextAreaElement,
  callback: (value: string) => void,
  className: string
) {
  let timer: ReturnType<typeof setTimeout>;
  input?.addEventListener('keyup', () => {
    const { value } = input;

    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      onVisibleHandler(input, className);
      callback && callback(value);
    }, 400);
  });
}

export function onVisibleHandler(
  input: HTMLInputElement | HTMLTextAreaElement,
  className: string
) {
  const isActive = document.activeElement;
  const dropdown = document.querySelector(className);

  if (isActive !== input || input.value.length === 0) {
    dropdown?.classList.remove('active');
  } else {
    dropdown?.classList.add('active');
  }
}

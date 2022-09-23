export class SubmitBtn {
    constructor() {
        this.form = document.querySelector('form');
        this.submitBtn = document.querySelector('.form__submit-button');
    }

    onSubmit() {
        this.submitBtn.addEventListener('click', (e) => {
            this.form.submit();
        })
    }
}

export class ImgUploadBtn {
    constructor(element) {
        this.uploadBtn = document.querySelector(element);
        this.fileInput = document.querySelector(element + ' #file-input');

        this.wrapper = document.querySelector(element + " .swiper-wrapper");
        this.nextBtn = document.querySelector(element + " .swiper-button-next");
        this.prevBtn = document.querySelector(element + " .swiper-button-prev");
    }

    onUpload() {
        this.uploadBtn.addEventListener('click', (e) => {
            if (e.target == this.nextBtn || e.target == this.prevBtn) {
                return;
            }
            this.fileInput.click();
            this.fileInput.addEventListener('change', this._getImageFile);
        })
    }

    _getImageFile = (e) => {
        const uploadFiles = [];
        const files = e.currentTarget.files;

        if ([...files].length == 0) {
            return;
        }

        if ([...files].length > 10) {
            alert('이미지는 최대 10개까지 업로드가 가능합니다.');
            return;
        }

        [...files].forEach(file => {
            if (!file.type.match("image/.*")) {
                alert('이미지 파일만 업로드가 가능합니다.');
                return;
            }
        });

        this._init();

        [...files].forEach(file => {
            uploadFiles.push(file);
            const reader = new FileReader();
            reader.onload = (e) => {
                const photo = this._createPhoto(e, file);
                this.wrapper.appendChild(photo);
            }
            reader.readAsDataURL(file);
        })
    }

    
    _init() {
    const photoBtn = document.querySelector('.photo-btn');
        if (photoBtn != null) {
            photoBtn.remove();
        }

        this.nextBtn.style.display = 'block';
        this.prevBtn.style.display = 'block';

        if (this.wrapper.hasChildNodes) {
            this.wrapper.querySelectorAll('.swiper-slide').forEach(slide => {
                slide.remove();
            })
        }
    }

    _createPhoto(e, file) {
        const photo = createElement('div', 'class', 'swiper-slide');
        const img = createElement('img', 'src', e.target.result);
        img.setAttribute('data-file', file.name);
    
        photo.appendChild(img);
    
        return photo;
    }
}

function createElement(e, attr, attrName) {
    const element = document.createElement(e);
    element.setAttribute(attr, attrName);
    return element;
}
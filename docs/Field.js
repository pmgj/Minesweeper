export default class Field {
    constructor(value, state) {
        this.value = value;
        this.state = state;
    }

    getValue() {
        return this.value;
    }

    setValue(value) {
        this.value = value;
    }

    getState() {
        return this.state;
    }

    setState(newState) {
        this.state = newState;
    }

    toString() {
        return `(${this.value}, ${this.state})`;
    }
}
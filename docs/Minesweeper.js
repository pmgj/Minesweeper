import FieldValue from "./FieldValue.js";
import Field from "./Field.js";
import State from "./State.js";
import Winner from "./Winner.js";

export default class Minesweeper {
    constructor(rows, cols, bombs) {
        this.rows = rows;
        this.cols = cols;
        this.bombs = bombs;
        this.matrix = Array(this.rows).fill().map(() => Array(this.cols).fill());
        this.lastMove = null;
    }
    getRows() {
        return this.rows;
    }
    getCols() {
        return this.cols;
    }
    getBombs() {
        return this.bombs;
    }
    createMatrix() {
        for (let i = 0; i < this.rows; i++) {
            for (let j = 0; j < this.cols; j++) {
                this.matrix[i][j] = new Field(FieldValue.NONE, State.HIDE);
            }
        }
        for (let i = 0; i < this.bombs;) {
            let row = Math.floor((Math.random() * this.rows));
            let col = Math.floor((Math.random() * this.cols));
            if (this.matrix[row][col].getValue() === FieldValue.NONE) {
                this.matrix[row][col].setValue(FieldValue.BOMB);
                i++;
            }
        }
        for (let i = 0; i < this.rows; i++) {
            for (let j = 0; j < this.cols; j++) {
                if (this.matrix[i][j].getValue() === FieldValue.NONE) {
                    this.matrix[i][j].setValue(this.countPosition(i, j));
                }
            }
        }
    }
    countPosition(row, col) {
        let count = 0;
        for (let i = row - 1; i <= row + 1; i++) {
            for (let j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < this.rows && j >= 0 && j < this.cols && this.matrix[i][j].getValue() === FieldValue.BOMB) {
                    count++;
                }
            }
        }
        return count;
    }
    getNumOfFlags() {
        return this.bombs - this.matrix.flat().filter(x => x.getState() == State.FLAG).length;
    }
    endOfGame() {
        let blocked = this.matrix.flat().filter(x => x.getState() != State.SHOW).length;
        return blocked === this.bombs ? Winner.WIN : Winner.NONE;
    }
    openCell(row, col) {
        let f = this.matrix[row][col];
        if (f.getValue() != FieldValue.BOMB) {
            if (f.getValue() == FieldValue.NONE && f.getState() == State.HIDE) {
                this.showCell(row, col);
                this.openCells(row, col);
            } else {
                this.showCell(row, col);
            }
        }
    }
    showCell(row, col) {
        let f = this.matrix[row][col];
        f.setState(State.SHOW);
    }
    openCells(row, col) {
        let rowMax = this.rows, colMax = this.cols;
        for (let i = row - 1; i <= row + 1; i++) {
            for (let j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rowMax && j >= 0 && j < colMax) {
                    this.openCell(i, j);
                }
            }
        }
    }
    showBombs() {
        for (let i = 0; i < this.rows; i++) {
            for (let j = 0; j < this.cols; j++) {
                let value = this.matrix[i][j].getValue();
                if (value == FieldValue.BOMB) {
                    this.showCell(i, j);
                }
            }
        }
    }
    play(row, col, state) {
        if (this.lastMove == Winner.LOSE || this.lastMove == Winner.WIN) {
            return Winner.NONE;
        }
        let f = this.matrix[row][col];
        let ret = Winner.NONE;
        switch (state) {
            case State.FLAG:
                if (f.getState() == State.HIDE) {
                    f.setState(State.FLAG);
                } else if (f.getState() == State.FLAG) {
                    f.setState(State.HIDE);
                }
                break;
            case State.SHOW:
                if (f.getState() == State.HIDE) {
                    ret = this.play2(row, col);
                }
            default:
                break;
        }
        return ret;
    }
    play2(row, col) {
        let ret = null;
        let f = this.matrix[row][col];
        switch (f.getValue()) {
            case FieldValue.BOMB:
                ret = Winner.LOSE;
                break;
            case FieldValue.NONE:
                this.openCell(row, col);
                ret = this.endOfGame();
                break;
            default:
                this.showCell(row, col);
                ret = this.endOfGame();
                break;
        }
        if (ret == Winner.WIN || ret == Winner.LOSE) {
            this.showBombs();
        }
        this.lastMove = ret;
        return ret;
    }
    getHiddenMatrix() {
        let ret = Array(this.rows).fill().map(() => Array(this.cols).fill());
        for (let i = 0; i < this.rows; i++) {
            for (let j = 0; j < this.cols; j++) {
                let cell = new Field(this.matrix[i][j].getValue(), this.matrix[i][j].getState());
                ret[i][j] = cell;
                if (cell.getState() != State.SHOW) {
                    ret[i][j].setValue(FieldValue.NONE);
                }
            }
        }
        return ret;
    }
}
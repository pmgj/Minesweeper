import FieldValue from "./FieldValue.js";
import Minesweeper from "./Minesweeper.js";
import State from "./State.js";
import Winner from "./Winner.js";

class GUI {
    constructor() {
        this.cm = null;
        this.table = null;
        this.difficulties = [{ name: "Beginner", rows: 9, cols: 9, bombs: 10 }, { name: "Intermediate", rows: 16, cols: 16, bombs: 40 }, { name: "Advanced", rows: 16, cols: 40, bombs: 99 }];    
    }
    printMatrixTable() {
        this.table = document.querySelector("table");
        let inner = "";
        for (let i = 0; i < this.cm.getRows(); i++) {
            inner += "<tr>";
            for (let j = 0; j < this.cm.getCols(); j++) {
                inner += "<td class='blocked'></td>";
            }
            inner += "</tr>";
        }
        this.table.innerHTML = inner;
    }
    updateMatrixTable() {
        let hm = this.cm.getHiddenMatrix();
        for (let i = 0; i < this.cm.getRows(); i++) {
            for (let j = 0; j < this.cm.getCols(); j++) {
                let square = hm[i][j];
                let td = this.table.rows[i].cells[j];
                switch (square.getState()) {
                    case State.FLAG:
                        td.className = "flag";
                        td.innerHTML = "&#9873;";
                        break;
                    case State.HIDE:
                        td.className = "blocked";
                        td.innerHTML = "";
                        break;
                    case State.SHOW:
                        switch (square.getValue()) {
                            case FieldValue.BOMB:
                                td.className = "flag";
                                td.innerHTML = "&#128163;";
                                break;
                            case FieldValue.NONE:
                                td.className = "show";
                                td.innerHTML = "";
                                break;
                            default:
                                td.className = "b" + square.getValue();
                                td.innerHTML = square.getValue();
                        }
                        break;
                }
            }
        }
    }
    showMessage(msg) {
        let message = document.querySelector("#message");
        message.innerHTML = msg;
    }
    check(event) {
        let cell = event.target;
        let col = cell.cellIndex;
        let row = cell.parentNode.rowIndex;
        let m = this.cm.play(row, col, State.SHOW);
        if (m === Winner.LOSE) {
            this.showMessage("You lose! &#9785;");
            this.unsetEvents();
        } else if (m === Winner.WIN) {
            this.showMessage("You win! &#9786;");
            this.unsetEvents();
        }
        this.updateMatrixTable();
    }
    markBomb(event) {
        event.preventDefault();
        let cell = event.target;
        let col = cell.cellIndex;
        let row = cell.parentNode.rowIndex;
        this.cm.play(row, col, State.FLAG);
        this.updateMatrixTable();
        this.setNumOfBombs(this.cm.getNumOfFlags());
    }
    setEvents() {
        this.table.onclick = this.check.bind(this);
        this.table.oncontextmenu = this.markBomb.bind(this);
    }
    unsetEvents() {
        this.table.onclick = undefined;
        this.table.oncontextmenu = undefined;
    }
    setNumOfBombs(n) {
        let p = document.querySelector("#numBombs");
        p.textContent = n;
    }
    newGame() {
        let diff = document.querySelector("#difficulty");
        let value = (diff.value) ? parseInt(diff.value) : 0;
        let { rows, cols, bombs } = this.difficulties[value];
        this.cm = new Minesweeper(rows, cols, bombs);
        this.cm.createMatrix();
        this.setNumOfBombs(bombs);
        this.printMatrixTable();
        this.setEvents();
        this.showMessage("");
    }
    init() {
        let diff = document.querySelector("#difficulty");
        let str = "";
        this.difficulties.forEach((value, i) => {
            str += `<option value="${i}">${value.name} (${value.rows} x ${value.cols}, ${value.bombs} bombs)</option>`;
        });
        diff.innerHTML = str;
        let button = document.querySelector("input[type='button']");
        button.onclick = this.newGame.bind(this);
        this.newGame();
    }
}
let gui = new GUI();
gui.init();

import FieldValue from "./FieldValue.js";
import Minesweeper from "./Minesweeper.js";
import State from "./State.js";
import Winner from "./Winner.js";

function GUI() {
    let cm = null;
    let table = null;
    let difficulties = [{ name: "Beginner", rows: 9, cols: 9, bombs: 10 }, { name: "Intermediate", rows: 16, cols: 16, bombs: 40 }, { name: "Advanced", rows: 16, cols: 40, bombs: 99 }];
    function printMatrixTable() {
        table = document.querySelector("table");
        let inner = "";
        for (let i = 0; i < cm.getRows(); i++) {
            inner += "<tr>";
            for (let j = 0; j < cm.getCols(); j++) {
                inner += "<td class='blocked'></td>";
            }
            inner += "</tr>";
        }
        table.innerHTML = inner;
    }
    function updateMatrixTable() {
        let hm = cm.getHiddenMatrix();
        for (let i = 0; i < cm.getRows(); i++) {
            for (let j = 0; j < cm.getCols(); j++) {
                let square = hm[i][j];
                let td = table.rows[i].cells[j];
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
    function showMessage(msg) {
        let message = document.querySelector("#message");
        message.innerHTML = msg;
    }
    function check(event) {
        let cell = event.target;
        let col = cell.cellIndex;
        let row = cell.parentNode.rowIndex;
        let m = cm.play(row, col, State.SHOW);
        if (m === Winner.LOSE) {
            showMessage("You lose! &#9785;");
            unsetEvents();
        } else if (m === Winner.WIN) {
            showMessage("You win! &#9786;");
            unsetEvents();
        }
        updateMatrixTable();
    }
    function markBomb(event) {
        let cell = event.target;
        let col = cell.cellIndex;
        let row = cell.parentNode.rowIndex;
        cm.play(row, col, State.FLAG);
        updateMatrixTable();
        setNumOfBombs(cm.getNumOfFlags());
        event.preventDefault();
    }
    function setEvents() {
        table.onclick = check;
        table.oncontextmenu = markBomb;
    }
    function unsetEvents() {
        table.onclick = undefined;
        table.oncontextmenu = undefined;
    }
    function setNumOfBombs(n) {
        let p = document.querySelector("#numBombs");
        p.textContent = n;
    }
    function newGame() {
        let diff = document.querySelector("#difficulty");
        let value = (diff.value) ? parseInt(diff.value) : 0;
        let { rows, cols, bombs } = difficulties[value];
        cm = new Minesweeper(rows, cols, bombs);
        cm.createMatrix();
        setNumOfBombs(bombs);
        printMatrixTable();
        setEvents();
        showMessage("");
    }
    function init() {
        let diff = document.querySelector("#difficulty");
        let str = "";
        difficulties.forEach((value, i) => {
            str += `<option value="${i}">${value.name} (${value.rows} x ${value.cols}, ${value.bombs} bombs)</option>`;
        });
        diff.innerHTML = str;
        let button = document.querySelector("input[type='button']");
        button.onclick = newGame;
        newGame();
    }
    return { init };
}
let gui = new GUI();
gui.init();

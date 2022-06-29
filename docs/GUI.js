import {Cell} from "./Cell.js";
import {Minesweeper} from "./Minesweeper.js";

function GUI() {
    let cm = null;
    let table = null;
    let numFlags = 0;
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
    function showCell(row, col) {
        let cell = table.rows[row].cells[col];
        cell.className = "show";
        let str = cm.getCell(row, col);
        switch (cm.getCell(row, col)) {
            case Cell.BOMB:
                str = "&#128163;";
                cell.className = "flag";
                break;
            case Cell.EMPTY:
                str = "";
                break;
            default:
                cell.className = "b" + cm.getCell(row, col);
        }
        cell.innerHTML = str;
    }
    function openCell(row, col) {
        let cell = table.rows[row].cells[col];
        if (cm.getCell(row, col) !== Cell.BOMB) {
            if (cm.getCell(row, col) === Cell.EMPTY && cell.className === 'blocked') {
                showCell(row, col);
                openCells(row, col);
            } else {
                showCell(row, col);
            }
        }
    }
    function openCells(row, col) {
        let rowMax = cm.getRows(), colMax = cm.getCols();
        for (let i = row - 1; i <= row + 1; i++) {
            for (let j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rowMax && j >= 0 && j < colMax) {
                    openCell(i, j);
                }
            }
        }
    }
    function showBombs() {
        for (let i = 0; i < cm.getRows(); i++) {
            for (let j = 0; j < cm.getCols(); j++) {
                if (cm.getCell(i, j) === Cell.BOMB) {
                    showCell(i, j);
                }
            }
        }
    }
    function endOfGame() {
        let blocked = document.querySelectorAll(".blocked").length;
        if (blocked === numFlags) {
            showMessage("Você ganhou! &#9786;");
            showBombs();
            unsetEvents();
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
        let value = cm.getCell(row, col);
        if (cell.className !== 'flag') {
            if (value === Cell.BOMB) {
                showMessage("Você perdeu! &#9785;");
                showBombs();
                unsetEvents();
            } else if (value === Cell.EMPTY) {
                openCell(row, col);
                endOfGame();
            } else {
                showCell(row, col);
                endOfGame();
            }
        }
    }
    function markBomb(event) {
        let cell = event.target;
        if (cell.className === "flag") {
            cell.innerHTML = "";
            cell.className = "blocked";
            setNumOfBombs(++numFlags);
        } else if (cell.className === "blocked") {
            cell.innerHTML = "&#9873;";
            cell.className = "flag";
            setNumOfBombs(--numFlags);
        }
        endOfGame();
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
        let difficulties = [{rows: 9, cols: 9, bombs: 10}, {rows: 16, cols: 16, bombs: 40}, {rows: 16, cols: 40, bombs: 99}];
        let diff = document.querySelector("#difficulty");
        let value = (diff.value) ? parseInt(diff.value) : 0;
        let {rows, cols, bombs} = difficulties[value];
        cm = new Minesweeper(rows, cols, bombs);
        numFlags = bombs;
        setNumOfBombs(bombs);
        cm.fillBombs();
        printMatrixTable();
        setEvents();
        showMessage("");
    }
    function init() {
        let button = document.querySelector("input[type='button']");
        button.onclick = newGame;
        newGame();
    }
    return {init};
}
let gui = new GUI();
gui.init();

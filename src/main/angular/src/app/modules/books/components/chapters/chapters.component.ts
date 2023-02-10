import { Component, OnInit } from '@angular/core';
import {RouterModule} from "@angular/router";

@Component({
  selector: 'app-chapters',
  templateUrl: './chapters.component.html',
  styleUrls: ['./chapters.component.css']
})
export class ChaptersComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    console.log("ChaptersComponent");
  }

}

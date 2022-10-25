import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CategoryService} from "../../services/category.service";

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.css']
})
export class ConfirmComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<ConfirmComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private cateforyService: CategoryService) {
  }

  ngOnInit(): void {
    console.log("ConfirmComponent");
  }

  onDelete() {
    if (this.data.id) {
      this.cateforyService.deleteCategory(this.data.id).subscribe(data => {
        this.dialogRef.close(1);
      }, error => {
        this.dialogRef.close(2);
      });
    } else {
      this.dialogRef.close(2);
    }
  }

  cacel() {
    this.dialogRef.close(3);
  }
}

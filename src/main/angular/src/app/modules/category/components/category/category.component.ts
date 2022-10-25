import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoryService} from "../../../shared/services/category.service";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatDialog} from "@angular/material/dialog";
import {AddCategoryComponent} from "../add-category/add-category.component";
import {MatSnackBar, MatSnackBarRef, SimpleSnackBar} from "@angular/material/snack-bar";
import {ConfirmComponent} from "../../../shared/components/confirm/confirm.component";

export interface CategoryElement {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'description', 'actions'];
  dataSource = new MatTableDataSource<CategoryElement>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private categoryService: CategoryService, private dialog: MatDialog, private snakBar: MatSnackBar) {
  }


  ngOnInit(): void {
    this.getCategories();
  }

  getCategories() {
    this.categoryService.getCategories().subscribe(data => {
      console.log(data);
      this.processCategoryData(data);
    }, error => {
      console.log(error);
    });
  }

  processCategoryData(data: any) {
    const categories: CategoryElement[] = [];
    if (data.metadata[0].code == "200") {
      data.data.forEach((category: any) => {
        categories.push({
          id: category.id,
          name: category.name,
          description: category.description
        });
      });
      this.dataSource = new MatTableDataSource<CategoryElement>(categories);
      this.dataSource.paginator = this.paginator;
    }
  }

  openAddCategoryDialog() {
    const dialogRef = this.dialog.open(AddCategoryComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if (result == 1) {
        this.openSnackBar("Category added successfully", "Close");
        this.getCategories();
      } else if (result == 2) {
        this.openSnackBar("Error adding category", "Close");
      }
    });
  }

  openSnackBar(message: string, action: string): MatSnackBarRef<SimpleSnackBar> {
    return this.snakBar.open(message, action, {
      duration: 2000,
    });
  }


  editCategory(element: any) {
    const dialogRef = this.dialog.open(AddCategoryComponent, {
      width: '500px',
      data: element
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if (result == 1) {
        this.openSnackBar("Category updated successfully", "Close");
        this.getCategories();
      } else if (result == 2) {
        this.openSnackBar("Error updating category", "Close");
      }
    });
  }

  deleteCategory(id: number) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      width: '500px',
      data: {
        title: "Delete Category",
        message: "Are you sure you want to delete this category?",
        id: id
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == 1) {
        this.openSnackBar("Category deleted successfully", "Close");
        this.getCategories();
      } else if (result == 2) {
        this.openSnackBar("Error deleting category", "Close");
      }
    });
  }

  findBy(value: string) {
    if (value == "") {
      this.getCategories();
    } else {
      this.categoryService.getCategoryById(value).subscribe(data => {
        this.processCategoryData(data);
      }, error => {
        console.log(error);
      });
    }
  }
}
